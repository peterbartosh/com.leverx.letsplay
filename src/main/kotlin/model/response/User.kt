package model.response

import model.basic.Skill
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val username: String,
    val email: String,
    val age: Int? = null,
    val rating: Float? = null,
    val skills: List<Skill>? = null
)
