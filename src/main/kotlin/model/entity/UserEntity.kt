package model.entity

data class UserEntity(
    val id: Long,
    val username: String,
    val email: String,
    val password: String,
    val age: Int? = null,
    val rating: Float? = null,
    val skills: String? = null
)