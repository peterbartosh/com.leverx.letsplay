package data.tables.events

import data.tables.DatabaseFactory.dbQuery
import data.tables.locations.Locations
import model.request.SearchEventFilter
import model.entity.SportEventEntity
import data.tables.events_users.EventsUsersDao
import data.utils.isNotNullOp
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

fun isFinished(sportEventDomain: SportEventEntity) =
    sportEventDomain.date + sportEventDomain.startTimeMinutes * 60 * 1000 <=
            System.currentTimeMillis()

class EventsDaoImpl(
    private val eventsUsersDao: EventsUsersDao
) : EventsDao {

    override suspend fun getRemainingEventsAmount(userId: Long) = dbQuery {
        val result = eventsUsersDao.getByUserId(userId)
        result.getOrNull()?.count { eventUser ->
            val eventResult = getEventById(eventUser.eventId).getOrNull() ?: return@count false
            !isFinished(eventResult)
        } ?: 0
    }

    override suspend fun getEventById(eventId: Long) = dbQuery {
        Events.select {
            Events.id.eq(eventId)
        }.map(::rowToEvent).singleOrNull()
    }

    override suspend fun getUserEvents(userId: Long) = dbQuery {
        eventsUsersDao.getByUserId(userId).getOrNull()?.mapNotNull {
            Events.select {
                Events.id.eq(it.eventId)
            }.map(::rowToEvent).singleOrNull()
        } ?: emptyList()
    }

    override suspend fun searchEvents(
        searchEventFilter: SearchEventFilter?
    ) = dbQuery {

        println("1 ${searchEventFilter == null}")

        if (searchEventFilter == null) {
            Events.selectAll().map(::rowToEvent)
        } else {
            val query = if (searchEventFilter.locationFilter == null) {
                val eventsFilter = Events.eventsFilter(searchEventFilter)
                Events.select { eventsFilter }

            } else {
                val eventsFilter = Events.eventsFilter(searchEventFilter)
                val locationsFilter = Locations.locationsFilters(searchEventFilter.locationFilter)
                val finalFilter = eventsFilter.and(locationsFilter)

                Events.leftJoin(Locations).select { finalFilter }
            }

            println("2 $query")

            val resultData = query.apply {
                searchEventFilter.limit?.let { limit ->
                    searchEventFilter.skip?.let { skip ->
                        limit(n = limit, offset = skip.toLong())
                    } ?: limit(n = limit)
                }
            }.map(::rowToEvent)
            println("3 $resultData")
            resultData
        }
    }

    override suspend fun createEvent(
        sportEventEntity: SportEventEntity,
        adminId: Long
    ) = dbQuery {
        val insertStatementResult = dbQuery {
            Events.insert {
                it[locationId] = sportEventEntity.locationId
                it[date] = sportEventEntity.date
                it[startTimeMinutes] = sportEventEntity.startTimeMinutes
                it[endTimeMinutes] = sportEventEntity.endTimeMinutes
                it[sportType] = sportEventEntity.sportType
                it[skillLevel] = sportEventEntity.skillLevel
                it[minParticipatorsAmount] = sportEventEntity.minParticipatorsAmount
                it[maxParticipatorsAmount] = sportEventEntity.maxParticipatorsAmount
            }
        }

        insertStatementResult.getOrNull()?.resultedValues?.singleOrNull()?.let { row ->
            val entity = rowToEvent(row)
            eventsUsersDao.subscribeUserToEvent(
                userId = adminId,
                eventId = entity.id,
                isAdmin = true
            )
            entity
        }
    }

    override suspend fun editEvent(
        sportEventId: Long,
        sportEventEntity: SportEventEntity?
    ) = dbQuery {
        Events.update(
            where = { Events.id eq sportEventId }
        ) {
            sportEventEntity?.locationId?.let { _ ->
                it[locationId] = locationId
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

    override suspend fun deleteEvent(eventId: Long) = dbQuery {
        Events.deleteWhere { id eq eventId }
    }

    override suspend fun clearAll() = dbQuery {
        Events.deleteAll()
    }
}

fun rowToEvent(row: ResultRow) = SportEventEntity(
    id = row[Events.id],
    locationId = row[Events.locationId],
    sportType = row[Events.sportType],
    date = row[Events.date],
    startTimeMinutes = row[Events.startTimeMinutes],
    endTimeMinutes = row[Events.endTimeMinutes],
    minParticipatorsAmount = row[Events.minParticipatorsAmount],
    maxParticipatorsAmount = row[Events.maxParticipatorsAmount],
    skillLevel = row[Events.skillLevel]
)
