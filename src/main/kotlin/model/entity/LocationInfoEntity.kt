package model.entity

data class LocationInfoEntity(
    val id: Long,
    val country: String,
    val city: String,
    val address: String,
    val longitude: Double,
    val latitude: Double
)
