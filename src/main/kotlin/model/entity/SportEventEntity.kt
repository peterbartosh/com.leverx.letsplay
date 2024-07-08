package model.entity

data class SportEventEntity(
    val id: Long,
    val adminId: Long,
    val locationId: Long,
    val sportType: Int,
    val date: Long,
    val startTimeMinutes: Int,
    val endTimeMinutes: Int,
    val minParticipatorsAmount: Int? = null,
    val maxParticipatorsAmount: Int? = null,
    val skillLevel: Int? = null
)