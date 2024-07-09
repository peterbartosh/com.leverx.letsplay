package data.tables.tokens

import io.ktor.server.auth.*
import model.entity.TokensEntity
import model.entity.UserEntity

interface TokensDao {

    suspend fun getTokensByAccessToken(accessToken: String): TokensEntity?

    suspend fun addTokens(tokensEntity: TokensEntity): TokensEntity?

    suspend fun editTokens(tokensEntity: TokensEntity): TokensEntity?

    suspend fun deleteTokens(userId: Long): Boolean
}