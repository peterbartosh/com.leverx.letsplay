package model.request

import kotlinx.serialization.Serializable
import model.basic.Location

@Serializable
data class LocationInfoBody(
    val id: String,
    val country: String,
    val city: String,
    val address: String,
    val location: Location
)
