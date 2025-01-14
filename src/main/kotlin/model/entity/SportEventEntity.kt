package model.entity

import kotlinx.serialization.Serializable

@Serializable
data class SportEventEntity(
    val id: Long,
    val locationId: String,
    val sportType: Int,
    val date: Long,
    val startTimeMinutes: Int,
    val endTimeMinutes: Int,
    val minParticipatorsAmount: Int? = null,
    val maxParticipatorsAmount: Int? = null,
    val skillLevel: Int? = null
)