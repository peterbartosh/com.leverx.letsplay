package model.basic

import kotlinx.serialization.Serializable

@Serializable
data class Skill(
    val sportType: Int,
    val skillLevel: Int
)
