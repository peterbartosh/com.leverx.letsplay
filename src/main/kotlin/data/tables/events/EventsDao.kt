package data.tables.events

import model.request.SearchEventFilter
import model.entity.SportEventEntity

interface EventsDao {

    suspend fun searchEvents(
        searchEventFilter: SearchEventFilter?
    ): Result<List<SportEventEntity>>

    suspend fun createEvent(
        sportEventEntity: SportEventEntity,
        copyId: Boolean = false
    ): Result<SportEventEntity?>

    suspend fun editEvent(
        sportEventId: Long,
        sportEventEntity: SportEventEntity? = null
    ): Result<Int>

    suspend fun deleteEvent(eventId: Long): Result<Int>

    suspend fun clearAll(): Result<Int>
}