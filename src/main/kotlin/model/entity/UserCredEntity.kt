package model.entity

data class UserCredEntity(
    val userId: Long,
    val password: String,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)