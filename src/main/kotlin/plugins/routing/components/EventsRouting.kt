package plugins.routing.components

import data.service.ClientService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import model.basic.Location
import model.request.LocationFilter
import model.request.SearchEventFilter
import model.request.SportEventBody
import plugins.routing.Routes
import plugins.routing.respondToCall


fun Application.configureEventsRouting(
    clientService: ClientService
) {

    routing {
        authenticate(Routes.Auth.authBearer) {
            get(Routes.getEventById) {
                call.request.queryParameters["event_id"]?.toLong()?.let { eventId ->
                    val response = clientService.getEventById(eventId)
                    call.respondToCall(response)
                } ?: call.respond(status = HttpStatusCode.BadRequest, "Event id is null")
            }
        }
        authenticate(Routes.Auth.authBearer) {
            post(Routes.getRemainingEvents) {
                call.request.queryParameters["user_id"]?.toLong()?.let { userId ->
                    val response = clientService.getRemainingEventsAmount(userId)
                    call.respondToCall(response)
                } ?: call.respond(status = HttpStatusCode.BadRequest, "User id is null")
            }
        }
        authenticate(Routes.Auth.authBearer) {
            post(Routes.createEvent) {
                val sportEventEntity = call.receive<SportEventBody>()
                val response = clientService.createEvent(sportEventBody = sportEventEntity)
                call.respondToCall(response)
            }
        }
        authenticate(Routes.Auth.authBearer) {
            get(Routes.getUserEvents) {
                val parameters = call.parameters
                parameters["user_id"]?.toLong()?.let { userId ->
                    call.respondToCall(clientService.getUserEvents(userId))
                } ?: call.respond(status = HttpStatusCode.BadRequest, "User id not presented")
            }
        }
        authenticate(Routes.Auth.authBearer) {
            get(Routes.searchEvents) {

                val queryParams = call.request.queryParameters

                val searchEventFilter = try {

                    val types = queryParams["types"]?.ifEmpty {
                        null
                    }?.split(",")?.map {
                        it.toInt()
                    }
                    val skillLevels = queryParams["skill_levels"]?.ifEmpty {
                        null
                    }?.split(",")?.map {
                        it.toInt()
                    }

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

                val response = clientService.searchEvents(searchEventFilter = searchEventFilter)
                call.respondToCall(response)
            }
        }
        authenticate(Routes.Auth.authBearer) {
            post(Routes.subscribeToEvent) {
                val queryParameters = call.request.queryParameters
                val userId = queryParameters.getOrFail("user_id").toLong()
                val eventId = queryParameters.getOrFail("event_id").toLong()
                val response = clientService.subscribeUserToEvent(
                    eventId = eventId,
                    userId = userId,
                    isAdmin = false
                )
                call.respondToCall(response)
            }
        }
    }
}