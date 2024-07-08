package plugins

import model.basic.Location
import model.entity.SportEventEntity
import model.request.LocationFilter
import model.request.SearchEventFilter
import data.source.RemoteSource
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

object Routes {
    const val createEvent = "api/events/create"
    const val searchEvents = "api/events/search"
    const val subscribeToEvent = "api/events/subscribe"
    const val getAbailableLocations = "api/locations/available"
    const val getLocationBookings = "api/locations/{location_id}/bookings"
    const val getUser = "api/users/{user_id}"
}

fun Application.configureRouting(remoteSource: RemoteSource) {
    configureEventsRouting(remoteSource)
    configureUsersRouting(remoteSource)
    configureLocationsRouting(remoteSource)
}

fun Application.configureEventsRouting(
    remoteSource: RemoteSource
) {
    routing {

        post(Routes.createEvent) {
            val sportEventEntity = call.receive<SportEventEntity>()
            val result = remoteSource.createEvent(sportEventEntity = sportEventEntity)
            call.respond("Success!")
        }

        get(Routes.searchEvents) {

            val queryParams = call.request.queryParameters

            val searchEventFilter = try {
                val types = queryParams["types"]?.split(",")?.map { it.toInt() }
                val skillLevels = queryParams["skill_levels"]?.split(",")?.map { it.toInt() }

                println(types)
                println(skillLevels)

                val minParticipatorsAmount = queryParams["min_participators_amount"]?.toInt()
                val maxParticipatorsAmount = queryParams["max_participators_amount"]?.toInt()

                val locationRadiusKilometers = queryParams["location_radius_kilometers"]?.toFloat()
                val currentLocationLongitude = queryParams["current_location_longitude"]?.toDouble()
                val currentLocationLatitude = queryParams["current_location_latitude"]?.toDouble()
                val currentLocation = if (currentLocationLongitude != null && currentLocationLatitude != null) {
                    Location(
                        longitude = currentLocationLongitude,
                        latitude = currentLocationLatitude
                    )
                } else null

                val startDate = queryParams["start_date"]?.toLong()
                val endDate = queryParams["end_date"]?.toLong()
                val startTime = queryParams["start_time"]?.toInt()
                val endTime = queryParams["end_time"]?.toInt()

                val skip = queryParams["skip"]?.toInt()
                val limit = queryParams["limit"]?.toInt()

                SearchEventFilter(
                    types = types,
                    skillLevels = skillLevels,
                    minParticipatorsAmount = minParticipatorsAmount,
                    maxParticipatorsAmount = maxParticipatorsAmount,
                    startDate = startDate,
                    endDate = endDate,
                    startTime = startTime,
                    endTime = endTime,
                    locationFilter = locationRadiusKilometers?.let {
                        currentLocation?.let {
                            LocationFilter(
                                locationRadiusKilometers = locationRadiusKilometers,
                                currentLocation = currentLocation
                            )
                        }
                    },
                    skip = skip,
                    limit = limit
                )
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                null
            }

            val listOfEvents = remoteSource.searchEvents(searchEventFilter = searchEventFilter)

            if (listOfEvents.isEmpty()) {
                call.respond("Failure. Empty result")
            } else {
                call.respond(listOfEvents)
            }
        }

        post(Routes.subscribeToEvent) {
            val queryParameters = call.request.queryParameters
            val userId = queryParameters.getOrFail("user_id").toLong()
            val eventId = queryParameters.getOrFail("event_id").toLong()
            val result = remoteSource.subscribeUserToEvent(
                eventId = eventId,
                userId = userId,
                isAdmin = false
            )
            call.respond("Success.")
        }
    }
}

fun Application.configureLocationsRouting(
    remoteSource: RemoteSource
) {
    routing {
        get(Routes.getAbailableLocations) {
            val queryParams = call.request.queryParameters

            val minParticipatorsAmount = queryParams["min_participators_amount"]?.toInt()
            val maxParticipatorsAmount = queryParams["max_participators_amount"]?.toInt()

            val locationRadiusKilometers = queryParams["location_radius_kilometers"]?.toFloat()
            val currentLocationLongitude = queryParams["current_location_longitude"]?.toDouble()
            val currentLocationLatitude = queryParams["current_location_latitude"]?.toDouble()
            val currentLocation = if (currentLocationLongitude != null && currentLocationLatitude != null) {
                Location(
                    longitude = currentLocationLongitude,
                    latitude = currentLocationLatitude
                )
            } else null

            val sportType = queryParams["sport_type"]?.toInt()
            val startDate = queryParams["start_date"]?.toLong()
            val endDate = queryParams["end_date"]?.toLong()
            val startTime = queryParams["start_time"]?.toInt()
            val endTime = queryParams["end_time"]?.toInt()

            val skip = queryParams["skip"]?.toInt()
            val limit = queryParams["limit"]?.toInt()

            val searchEventFilter = SearchEventFilter(
                types = emptyList(),
                skillLevels = emptyList(),
                minParticipatorsAmount = minParticipatorsAmount,
                maxParticipatorsAmount = maxParticipatorsAmount,
                startDate = startDate,
                endDate = endDate,
                startTime = startTime,
                endTime = endTime,
                locationFilter = locationRadiusKilometers?.let {
                    currentLocation?.let {
                        LocationFilter(
                            locationRadiusKilometers = locationRadiusKilometers,
                            currentLocation = currentLocation
                        )
                    }
                },
                skip = skip,
                limit = limit
            )

            val result = remoteSource.getSportLocations(searchEventFilter = searchEventFilter)
            if (result.isEmpty()) {
                call.respond("Failure. Empty result.")
            } else {
                call.respond(result)
            }

        }

        get(Routes.getLocationBookings) {
            val locationId = call.parameters["location_id"]?.toLong()
            if (locationId == null) {
                call.respond("Failure. Location not found")
            } else {
                val result = remoteSource.getLocationBookings(locationId = locationId)
                if (result.isEmpty()) {
                    call.respond("Failure. Empty result.")
                } else {
                    call.respond(result)
                }
            }
        }
    }
}

fun Application.configureUsersRouting(
    remoteSource: RemoteSource
) {
    routing {
        get(Routes.getUser) {
            val userId = call.parameters["user_id"]?.toLong()
            if (userId == null) {
                call.respond("Failure. User not found")
            } else {
                val result = remoteSource.getUser(userId)
                if (result == null) {
                    call.respond("Failure. User not found")
                } else {
                    call.respond(result)
                }
            }
        }
    }
}
