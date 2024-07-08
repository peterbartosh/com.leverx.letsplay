package data.tables.events_users

import data.model.entity.EventUserEntity

interface EventsUsersDao {

    suspend fun subscribeUserToEvent(
        userId: Long,
        eventId: Long,
        isAdmin: Boolean
    ): Boolean

    suspend fun getByEventId(
        eventId: Long
    ): List<EventUserEntity>

    suspend fun getAdmin(
        eventId: Long
    ): EventUserEntity?

    suspend fun getByUserId(
        userId: Long
    ): List<EventUserEntity>

}