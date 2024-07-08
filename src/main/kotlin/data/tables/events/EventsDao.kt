package data.tables.events

import data.model.request.SearchEventFilter
import data.model.entity.SportEventEntity

interface EventsDao {

    suspend fun searchEvents(
        searchEventFilter: SearchEventFilter?
    ): List<SportEventEntity>

    suspend fun createEvent(sportEventEntity: SportEventEntity)

    suspend fun editEvent(
        sportEventId: Long,
        sportEventEntity: SportEventEntity? = null
    )

    suspend fun deleteEvent(eventId: Long)

    suspend fun clearAll()
}