package data.model.response

import data.model.basic.Location
import kotlinx.serialization.Serializable

@Serializable
data class LocationInfo(
    val id: Long,
    val country: String,
    val city: String,
    val address: String,
    val location: Location
)
