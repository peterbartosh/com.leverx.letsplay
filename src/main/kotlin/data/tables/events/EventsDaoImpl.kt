package data.tables.events

import data.tables.DatabaseFactory.dbQuery
import data.tables.locations.Locations
import data.model.request.SearchEventFilter
import data.model.entity.SportEventEntity
import data.tables.events_users.EventsUsersDao
import data.utils.isNotNullOp
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class EventsDaoImpl(
    private val eventsUsersDao: EventsUsersDao
) : EventsDao {

    override suspend fun searchEvents(
        searchEventFilter: SearchEventFilter?
    ): List<SportEventEntity> {
        return dbQuery {
            if (searchEventFilter == null) {
                Events.selectAll().map(::rowToEvent)
            } else if (searchEventFilter.locationFilter == null) {
                val eventsFilter = Events.eventsFilter(searchEventFilter)
                val query = if (eventsFilter.isNotNullOp()) {
                    Events.select { eventsFilter }
                } else {
                    Events.selectAll()
                }
                query.apply {
                    searchEventFilter.limit?.let { limit ->
                        searchEventFilter.skip?.let { skip ->
                            limit(n = limit, offset = skip.toLong())
                        } ?: limit(n = limit)
                    }
                }.map(::rowToEvent)
            } else {
                val eventsFilter = Events.eventsFilter(searchEventFilter)
                val locationsFilter = Locations.locationsFilters(searchEventFilter.locationFilter)
                val finalFilter = if (eventsFilter.isNotNullOp() && locationsFilter.isNotNullOp()) {
                    eventsFilter.and(locationsFilter)
                } else if (eventsFilter.isNotNullOp()) {
                    eventsFilter
                } else if (locationsFilter.isNotNullOp()) {
                    locationsFilter
                } else Op.nullOp()

                val query = if (finalFilter.isNotNullOp()) {
                    Events.leftJoin(Locations).select { finalFilter }
                } else {
                    Events.leftJoin(Locations).selectAll()
                }

                query.apply {
                    searchEventFilter.limit?.let { limit ->
                        searchEventFilter.skip?.let { skip ->
                            limit(n = limit, offset = skip.toLong())
                        } ?: limit(n = limit)
                    }
                }.map(::rowToEvent)
            }
        }
    }

    override suspend fun createEvent(
        sportEventEntity: SportEventEntity
    ) {
        val insertStatement = Events.insert {
            it[id] = sportEventEntity.id
            it[locationId] = sportEventEntity.locationId
            it[adminId] = sportEventEntity.adminId
            it[date] = sportEventEntity.date
            it[startTimeMinutes] = sportEventEntity.startTimeMinutes
            it[endTimeMinutes] = sportEventEntity.endTimeMinutes
            it[sportType] = sportEventEntity.sportType
            it[skillLevel] = sportEventEntity.skillLevel
            it[minParticipatorsAmount] = sportEventEntity.minParticipatorsAmount
            it[maxParticipatorsAmount] = sportEventEntity.maxParticipatorsAmount
        }
        eventsUsersDao.subscribeUserToEvent(
            userId = sportEventEntity.adminId,
            eventId = sportEventEntity.id,
            isAdmin = true
        )
        insertStatement.resultedValues?.singleOrNull()?.let(::rowToEvent)
    }

    override suspend fun editEvent(
        sportEventId: Long,
        sportEventEntity: SportEventEntity?
    ) {
        Events.update(
            where = { Events.id eq sportEventId }
        ) {
            sportEventEntity?.locationId?.let { _ ->
                it[locationId] = locationId
            }
            sportEventEntity?.adminId?.let { _ ->
                it[adminId] = adminId
            }
            sportEventEntity?.let { _ ->
                it[date] = sportEventEntity.date
                it[startTimeMinutes] = sportEventEntity.startTimeMinutes
                it[endTimeMinutes] = sportEventEntity.endTimeMinutes
                it[sportType] = sportEventEntity.sportType
                it[skillLevel] = sportEventEntity.skillLevel
                it[minParticipatorsAmount] = sportEventEntity.minParticipatorsAmount
                it[maxParticipatorsAmount] = sportEventEntity.maxParticipatorsAmount
            }
        }
    }

    override suspend fun deleteEvent(eventId: Long) {
        Events.deleteWhere { id eq eventId }
    }

    override suspend fun clearAll() {
        Events.deleteAll()
    }
}

fun rowToEvent(
    row: ResultRow
) = SportEventEntity(
    id = row[Events.id],
    adminId = row[Events.adminId],
    locationId = row[Events.locationId],
    sportType = row[Events.sportType],
    date = row[Events.date],
    startTimeMinutes = row[Events.startTimeMinutes],
    endTimeMinutes = row[Events.endTimeMinutes],
    minParticipatorsAmount = row[Events.minParticipatorsAmount],
    maxParticipatorsAmount = row[Events.maxParticipatorsAmount],
    skillLevel = row[Events.skillLevel]
)