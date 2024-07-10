package data.tables.events_users

import model.entity.EventUserEntity

interface EventsUsersDao {

    suspend fun subscribeUserToEvent(
        userId: Long,
        eventId: Long,
        isAdmin: Boolean
    ): Result<Boolean>

    suspend fun getByEventId(
        eventId: Long
    ): Result<List<EventUserEntity>>

    suspend fun getAdmin(
        eventId: Long
    ): Result<EventUserEntity?>

    suspend fun getByUserId(
        userId: Long
    ): Result<List<EventUserEntity>>

}