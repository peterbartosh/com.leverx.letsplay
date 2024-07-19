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
import io.ktor.http.*
import model.basic.Response
import model.entity.LocationInfoEntity
import model.request.SportEventBody
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class ClientService(
    private val locationsDao: LocationsDao,
    private val usersDao: UsersDao,
    private val eventsUsersDao: EventsUsersDao,
    private val eventsDao: EventsDao
) {

    suspend fun getEventById(eventId: Long): Response<SportEvent> {
        val result = eventsDao.getEventById(
            eventId = eventId
        )
        val fullEventData = result.getOrNull()?.fetchAdditionalEventData()
        return if (fullEventData != null) {
            Response.Data(body = fullEventData)
        } else {
            Response.Error(statusCode = HttpStatusCode.NotFound, "Result value is null")
        }
    }

    suspend fun getRemainingEventsAmount(userId: Long): Response<Int> {
        val result = eventsDao
            .getRemainingEventsAmount(userId = userId).getOrNull()
        return if (result == null) {
            Response.Error(statusCode = HttpStatusCode.NotFound, "")
        }  else {
            Response.Data(body = result)
        }
    }


    suspend fun getUserEvents(userId: Long): Response<List<SportEvent>> {
        val result = eventsDao.getUserEvents(
            userId = userId
        ).map { it.mapToAugmentedEvents() }.getOrNull()
        return if (result.isNullOrEmpty()) {
            Response.Error(statusCode = HttpStatusCode.NotFound, message = "No events presented")
        } else {
            Response.Data(body = result)
        }
    }

    suspend fun createEvent(sportEventBody: SportEventBody) = newSuspendedTransaction {

        val foundLocation = locationsDao.getLocationById(
            locationId = sportEventBody.locationInfoBody.id
        ).getOrNull()

        val locationId = foundLocation?.id ?: locationsDao.addLocation(
            location = LocationInfoEntity(
                id = sportEventBody.locationInfoBody.id,
                country = sportEventBody.locationInfoBody.country,
                city = sportEventBody.locationInfoBody.city,
                address = sportEventBody.locationInfoBody.address,
                longitude = sportEventBody.locationInfoBody.location.longitude,
                latitude = sportEventBody.locationInfoBody.location.latitude
            )
        ).getOrNull()?.id

        if (locationId == null) {
            this.rollback()
            return@newSuspendedTransaction Response.Error(
                statusCode = HttpStatusCode.NotFound,
                message = "Location not found"
            )
        }

        val result = eventsDao.createEvent(
            sportEventEntity = SportEventEntity(
                id = 0,
                locationId = locationId,
                sportType = sportEventBody.sportType,
                date = sportEventBody.date,
                startTimeMinutes = sportEventBody.startTimeMinutes,
                endTimeMinutes = sportEventBody.endTimeMinutes,
                minParticipatorsAmount = sportEventBody.minParticipatorsAmount,
                maxParticipatorsAmount = sportEventBody.maxParticipatorsAmount,
                skillLevel = sportEventBody.skillLevel
            ),
            adminId = sportEventBody.adminId
        )
        result.getOrNull()?.let { Response.Data(body = it) } ?: Response.Error(
            statusCode = HttpStatusCode.NotFound,
            message = ""
        )
    }

    suspend fun searchEvents(searchEventFilter: SearchEventFilter?): Response<List<SportEvent>> {
        val result = eventsDao.searchEvents(
            searchEventFilter = searchEventFilter
        ).getOrNull().mapToAugmentedEvents()

        return if (result.isEmpty()) {
            Response.Error(statusCode = HttpStatusCode.NotFound, "Empty result")
        } else {
            Response.Data(body = result)
        }
    }

    suspend fun subscribeUserToEvent(
        userId: Long,
        eventId: Long,
        isAdmin: Boolean
    ) = eventsUsersDao.subscribeUserToEvent(
        eventId = eventId,
        userId = userId,
        isAdmin = isAdmin
    ).getOrNull()?.let {
        if (it) {
            Response.Data(body = it)
        } else {
            Response.Error(statusCode = HttpStatusCode.Conflict)
        }
    } ?: Response.Error(statusCode = HttpStatusCode.NotFound, "")


    suspend fun getSportLocations(searchEventFilter: SearchEventFilter) =
        locationsDao.getSportLocations(searchEventFilter).getOrNull()?.map {
            it.toDto()
        }?.let {
            Response.Data(body = it)
        } ?: Response.Error(
            statusCode = HttpStatusCode.NotFound,
            message = ""
        )


    suspend fun getLocationBookings(locationId: Long) =
        locationsDao.getLocationEvents(locationId = locationId).getOrNull()?.map {
            Booking(
                dateAndTime = it.date + it.startTimeMinutes.toLong() * 60 * 1000,
                durationMinutes = it.endTimeMinutes - it.startTimeMinutes
            )
        }?.let {
            Response.Data(body = it)
        } ?: Response.Error(statusCode = HttpStatusCode.NotFound, message = "")

    suspend fun getUser(userId: Long) = usersDao.getUser(userId).getOrNull()?.toDto()?.let {
        Response.Data(body = it)
    } ?: Response.Error(statusCode = HttpStatusCode.NotFound, "")


    private suspend fun List<SportEventEntity?>?.mapToAugmentedEvents() = this?.mapNotNull {
        it?.fetchAdditionalEventData()
    } ?: emptyList()

    private suspend fun SportEventEntity.fetchAdditionalEventData() = newSuspendedTransaction {

        val admin = eventsUsersDao.getAdmin(eventId = this@fetchAdditionalEventData.id)
            .getOrNull()?.userId?.let { userId ->
                usersDao.getUser(userId = userId).getOrNull()?.toDto()
            }

        println("111 $admin")
        if (admin == null) {
            this.rollback()
            return@newSuspendedTransaction null
        }

        val participators = eventsUsersDao.getByEventId(eventId = this@fetchAdditionalEventData.id)
            .getOrNull()?.map { eventUser ->
                usersDao.getUser(userId = eventUser.userId).getOrNull()?.toDto()
            }

        println("222 $participators")
        if (participators == null) {
            this.rollback()
            return@newSuspendedTransaction null
        }

        val locationInfo = locationsDao.getLocationById(locationId = this@fetchAdditionalEventData.locationId)
            .getOrNull()?.toDto()

        println("333 $locationInfo")
        if (locationInfo == null) {
            this.rollback()
            return@newSuspendedTransaction null
        }

        this@fetchAdditionalEventData.toDto(
            admin = admin,
            participators = participators.filterNotNull(),
            locationData = locationInfo
        )
    }


}