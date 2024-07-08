package app.plugins

import data.model.basic.Location
import data.model.entity.SportEventEntity
import data.model.request.LocationFilter
import data.model.request.SearchEventFilter
import data.source.RemoteSource
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Application.configureRouting(remoteSource: RemoteSource) {
    configureEventsRouting(remoteSource)
    configureUsersRouting(remoteSource)
    configureLocationsRouting(remoteSource)
}

fun Application.configureEventsRouting(
    remoteSource: RemoteSource
) {
    routing {

        post("api/events/create") {
            val sportEventEntity = call.receive<SportEventEntity>()
            val result = remoteSource.createEvent(sportEventEntity = sportEventEntity)
            call.respond("Success!")
        }

        get("api/events/search") {

            val queryParams = call.request.queryParameters

            val searchEventFilter = try {
                val types = queryParams["types"]

                println(types)

                val skillLevels = queryParams["skill_levels"]

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

        post("api/events/subscribe") {
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
        get("api/locations/sport") {
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
                call.respond("Failure. Empty resulttt.")
            } else {
                call.respond(result)
            }

        }

        get("api/locations/{location_id}/bookings") {
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
        get("api/users/{user_id}") {
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
