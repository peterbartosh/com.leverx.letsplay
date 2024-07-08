package data.model.request

import data.model.basic.Location

data class LocationFilter(
    val locationRadiusKilometers: Float,
    val currentLocation: Location
)