package data.model.basic

import kotlinx.serialization.Serializable

@Serializable
data class Skill(
    val sportType: Int,
    val skillLevel: Int
)
