package data.service

import data.converter.toDto
import model.entity.SportEventEntity
import model.request.SearchEventFilter
import model.response.Booking
import model.response.LocationInfo
import model.response.SportEvent
import model.response.User
import data.tables.events.EventsDao
import data.tables.events_users.EventsUsersDao
import data.tables.locations.LocationsDao
import data.tables.users.UsersDao
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.postgresql.util.PSQLException
import org.postgresql.util.PSQLState

class ClientService(
    private val locationsDao: LocationsDao,
    private val usersDao: UsersDao,
    private val eventsUsersDao: EventsUsersDao,
    private val eventsDao: EventsDao
) {

    suspend fun createEvent(sportEventEntity: SportEventEntity) {
        eventsDao.createEvent(
            sportEventEntity = sportEventEntity
        )
    }

    suspend fun searchEvents(
        searchEventFilter: SearchEventFilter?
    ): List<SportEvent> {

        return eventsDao.searchEvents(searchEventFilter = searchEventFilter).getOrNull()?.mapNotNull { event ->
            newSuspendedTransaction {

                val admin = eventsUsersDao.getAdmin(eventId = event.id)
                    .getOrNull()?.userId?.let { userId ->
                        usersDao.getUser(userId = userId).getOrNull()?.toDto()
                    }

                if (admin == null) {
                    this.rollback()
                    return@newSuspendedTransaction null
                }

                val participators = eventsUsersDao.getByEventId(eventId = event.id)
                    .getOrNull()?.map { eventUser ->
                        usersDao.getUser(userId = eventUser.userId).getOrNull()?.toDto()
                    }

                if (participators == null) {
                    this.rollback()
                    return@newSuspendedTransaction null
                }

                val locationInfo = locationsDao.getLocationById(locationId = event.locationId)
                    .getOrNull()?.toDto()

                if (locationInfo == null) {
                    this.rollback()
                    return@newSuspendedTransaction null
                }

                event.toDto(
                    admin = admin,
                    participators = participators.filterNotNull(),
                    locationData = locationInfo
                )
            }
        } ?: emptyList()
    }

    suspend fun subscribeUserToEvent(
        userId: Long,
        eventId: Long,
        isAdmin: Boolean
    ): Boolean {
        return eventsUsersDao.subscribeUserToEvent(
            eventId = eventId,
            userId = userId,
            isAdmin = isAdmin
        ).getOrNull() == true
    }

    suspend fun getSportLocations(searchEventFilter: SearchEventFilter): List<LocationInfo> {
        return locationsDao.getSportLocations(searchEventFilter).getOrNull()?.map { it.toDto() } ?: emptyList()
    }

    suspend fun getLocationBookings(locationId: Long): List<Booking> {
        return locationsDao.getLocationEvents(locationId = locationId).getOrNull()?.map {
            Booking(
                dateAndTime = it.date + it.startTimeMinutes.toLong() * 60 * 1000,
                durationMinutes = it.endTimeMinutes - it.startTimeMinutes
            )
        } ?: emptyList()
    }

    suspend fun getUser(userId: Long): User? {
        return usersDao.getUser(userId).getOrNull()?.toDto()
    }

}