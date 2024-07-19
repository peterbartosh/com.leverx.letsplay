package plugins.routing.components

import data.service.ClientService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.basic.Location
import model.request.LocationFilter
import model.request.SearchEventFilter
import plugins.routing.Routes
import plugins.routing.respondToCall


fun Application.configureLocationsRouting(
    clientService: ClientService
) {
    routing {

        authenticate(Routes.Auth.authBearer) {
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

                val response = clientService.getSportLocations(searchEventFilter = searchEventFilter)
                call.respondToCall(response)
            }
        }

        authenticate(Routes.Auth.authBearer) {
            get(Routes.getLocationBookings) {
                val locationId = call.parameters["location_id"]?.toLong()
                if (locationId == null) {
                    call.respond("Failure. Location not found")
                } else {
                    val response = clientService.getLocationBookings(locationId = locationId)
                    call.respondToCall(response)
                }
            }
        }
    }
}