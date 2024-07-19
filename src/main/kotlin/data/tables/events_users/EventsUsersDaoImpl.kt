package data.tables.events_users

import data.tables.DatabaseFactory.dbQuery
import model.entity.EventUserEntity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class EventsUsersDaoImpl : EventsUsersDao {

    override suspend fun subscribeUserToEvent(
        userId: Long,
        eventId: Long,
        isAdmin: Boolean
    ) = dbQuery {
        val isNotDuplicate = EventsUsers.select {
            EventsUsers.userId.eq(userId).and(EventsUsers.eventId.eq(eventId))
        }.toList().isEmpty()

        val alreadyHasAdmin = if (isAdmin) {
            EventsUsers.select(
                EventsUsers.eventId.eq(eventId)
            ).any { row -> row[EventsUsers.isAdmin] }
        } else {
            false
        }

        if (isNotDuplicate && !alreadyHasAdmin) {
            EventsUsers.insert {
                it[EventsUsers.eventId] = eventId
                it[EventsUsers.userId] = userId
                it[EventsUsers.isAdmin] = isAdmin
            }
        }
        isNotDuplicate && !alreadyHasAdmin
    }

    override suspend fun getByEventId(eventId: Long) = dbQuery {
        EventsUsers.select {
            EventsUsers.eventId eq eventId
        }.map(::rowToEventUser)
    }

    override suspend fun getByUserId(userId: Long) = dbQuery {
        EventsUsers.select {
            EventsUsers.userId eq userId
        }.map(::rowToEventUser)
    }

    override suspend fun clearAll(): Result<Boolean> = dbQuery {
        EventsUsers.deleteAll() == 1
    }

    override suspend fun getAdmin(eventId: Long) = dbQuery {
        EventsUsers.select {
            EventsUsers.eventId.eq(eventId).and(EventsUsers.isAdmin.eq(true))
        }.map(::rowToEventUser).singleOrNull()
    }

    private fun rowToEventUser(row: ResultRow): EventUserEntity {
        return EventUserEntity(
            id = row[EventsUsers.id],
            eventId = row[EventsUsers.eventId],
            userId = row[EventsUsers.userId],
            isAdmin = row[EventsUsers.isAdmin]
        )
    }

}