package model.request

import model.basic.Location

data class LocationFilter(
    val locationRadiusKilometers: Float,
    val currentLocation: Location
)