package model.request

import kotlinx.serialization.Serializable

@Serializable
data class SportEventBody(
    val adminId: Long,
    val sportType: Int,
    val date: Long,
    val startTimeMinutes: Int,
    val endTimeMinutes: Int,
    val minParticipatorsAmount: Int? = null,
    val maxParticipatorsAmount: Int? = null,
    val skillLevel: Int? = null,
    val locationInfoBody: LocationInfoBody
)