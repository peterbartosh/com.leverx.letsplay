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

class ClientService(
    private val locationsDao: LocationsDao,
    private val usersDao: UsersDao,
    private val eventsUsersDao: EventsUsersDao,
    private val eventsDao: EventsDao
) {

    suspend fun createEvent(sportEventEntity: SportEventEntity){
        return eventsDao.createEvent(
            sportEventEntity = sportEventEntity
        )
    }

    suspend fun searchEvents(
        searchEventFilter: SearchEventFilter?
    ): List<SportEvent> {
        return newSuspendedTransaction {
            eventsDao.searchEvents(
                searchEventFilter = searchEventFilter
            ).mapNotNull { event ->
                val admin = eventsUsersDao.getAdmin(eventId = event.id)?.userId?.let { userId ->
                    usersDao.getUser(userId = userId)?.toDto()
                }
                val participators = eventsUsersDao.getByEventId(eventId = event.id).map { eventUser ->
                    usersDao.getUser(userId = eventUser.userId)?.toDto()
                }
                val locationInfo = locationsDao.getLocationById(locationId = event.locationId)?.toDto()

                if (admin == null || participators.filterNotNull().isEmpty() || locationInfo == null) {
                    null
                } else {
                    event.toDto(
                        admin = admin,
                        participators = participators.filterNotNull(),
                        locationData = locationInfo
                    )
                }
            }
        }
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
        )
    }

    suspend fun getSportLocations(searchEventFilter: SearchEventFilter): List<LocationInfo> {
        return locationsDao.getSportLocations(searchEventFilter).map { it.toDto() }
    }

    suspend fun getLocationBookings(locationId: Long): List<Booking> {
        return locationsDao
            .getLocationBookings(locationId = locationId).map {
                Booking(
                    dateAndTime = it.date + it.startTimeMinutes.toLong() * 60 * 1000,
                    durationMinutes = it.endTimeMinutes - it.startTimeMinutes
                )
            }
    }

    suspend fun getUser(userId: Long): User? {
        return usersDao.getUser(userId)?.toDto()
    }

}