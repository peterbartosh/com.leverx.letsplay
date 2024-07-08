package data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class Booking(
    val dateAndTime: Long,
    val durationMinutes: Int
)
