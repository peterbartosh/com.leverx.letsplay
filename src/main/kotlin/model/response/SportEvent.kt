package model.response
import kotlinx.serialization.Serializable

@Serializable
data class SportEvent(
    val id: Long,
    val sportType: Int,
    val admin: User,
    val locationData: LocationInfo,
    val date: Long,
    val startTimeMinutes: Int,
    val endTimeMinutes: Int,
    val participators: List<User> = emptyList(),
    val minParticipatorsAmount: Int?,
    val maxParticipatorsAmount: Int?,
    val skillLevel: Int?
)
