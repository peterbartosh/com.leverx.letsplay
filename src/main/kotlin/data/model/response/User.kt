package data.model.response

import data.model.basic.Skill
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val name: String,
    val age: Int,
    val rating: Float,
    val skills: List<Skill>
)
