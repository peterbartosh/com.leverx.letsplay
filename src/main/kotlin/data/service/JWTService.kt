package data.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import data.tables.tokens.TokensDao
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import model.entity.TokensEntity
import org.koin.core.component.KoinComponent
import java.util.*

private const val accessTokenExpiresInDuration = 3_600_000
private const val refreshTokenExpiresInDuration = 86_400_000

class JwtService: KoinComponent {

    private val secret = "secret"

    fun createAccessToken(): String = createJwtToken(accessTokenExpiresInDuration)

    fun createRefreshToken(): String = createJwtToken(refreshTokenExpiresInDuration)

    fun accessTokenExpiresIn() = System.currentTimeMillis() + accessTokenExpiresInDuration

    fun refreshTokenExpiresIn() = System.currentTimeMillis() + refreshTokenExpiresInDuration

    private fun createJwtToken(expireIn: Int): String =
        JWT.create()
            .withExpiresAt(Date(System.currentTimeMillis() + expireIn))
            .sign(Algorithm.HMAC256(secret))
}