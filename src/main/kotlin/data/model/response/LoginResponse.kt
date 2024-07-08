package data.model.response
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val accessToken: String?,
    val expiresIn: Int?
)