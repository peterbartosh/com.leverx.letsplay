package data.tables.events_users

import data.tables.DatabaseFactory.dbQuery
import data.model.entity.EventUserEntity
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class EventsUsersDaoImpl : EventsUsersDao {

    override suspend fun subscribeUserToEvent(
        userId: Long,
        eventId: Long,
        isAdmin: Boolean
    ): Boolean {
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
        return isNotDuplicate && !alreadyHasAdmin
    }

    override suspend fun getByEventId(eventId: Long): List<EventUserEntity> = dbQuery {
        EventsUsers.select {
            EventsUsers.eventId eq eventId
        }.map(::rowToEventUser)
    }

    override suspend fun getByUserId(userId: Long): List<EventUserEntity> = dbQuery {
        EventsUsers.select {
            EventsUsers.userId eq userId
        }.map(::rowToEventUser)
    }

    override suspend fun getAdmin(eventId: Long): EventUserEntity? = dbQuery {
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