package model.request

import kotlinx.serialization.Serializable

@Serializable
data class SignInRequest(
    val usernameOrEmail: String,
    val password: String
)
