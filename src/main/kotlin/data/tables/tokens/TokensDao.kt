package data.tables.tokens

import io.ktor.server.auth.*
import model.entity.TokensEntity
import model.entity.UserEntity

interface TokensDao {

    suspend fun getTokensByAccessToken(accessToken: String): Result<TokensEntity?>

    suspend fun getTokensByRefreshToken(refreshToken: String): Result<TokensEntity?>

    suspend fun getTokensByUserId(userId: Long): Result<TokensEntity?>

    suspend fun addTokens(tokensEntity: TokensEntity): Result<TokensEntity?>

    suspend fun editTokens(tokensEntity: TokensEntity): Result<TokensEntity?>

    suspend fun deleteTokens(userId: Long): Result<Boolean>
}