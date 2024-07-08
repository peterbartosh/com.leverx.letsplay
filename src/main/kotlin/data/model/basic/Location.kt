package data.model.basic

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val longitude: Double,
    val latitude: Double
)
