package data.tables.tokens

import data.tables.DatabaseFactory.dbQuery
import model.entity.TokensEntity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class TokensDaoImpl: TokensDao {

    override suspend fun getTokensByAccessToken(accessToken: String): TokensEntity? = dbQuery {
        Tokens.select {
            Tokens.accessToken.eq(accessToken)
        }.map(::rowToTokens).singleOrNull()
    }

    override suspend fun addTokens(tokensEntity: TokensEntity): TokensEntity? {
        Tokens.insert {
            it[userId] = tokensEntity.userId
            it[accessToken] = tokensEntity.accessToken
            it[refreshToken] = tokensEntity.refreshToken
            it[accessTokenExpiresIn] = tokensEntity.accessTokenExpiresIn
            it[refreshTokenExpiresIn] = tokensEntity.refreshTokenExpiresIn
        }
        val foundTokens = Tokens.select{ Tokens.userId.eq(tokensEntity.userId) }.map(::rowToTokens)
        return if (foundTokens.size == 1) {
            foundTokens.first()
        } else null
    }

    override suspend fun editTokens(tokensEntity: TokensEntity): TokensEntity? {
        Tokens.update(
            where = { Tokens.userId.eq(tokensEntity.userId) }
        ) {
            it[userId] = tokensEntity.userId
            it[accessToken] = tokensEntity.accessToken
            it[refreshToken] = tokensEntity.refreshToken
            it[accessTokenExpiresIn] = tokensEntity.accessTokenExpiresIn
            it[refreshTokenExpiresIn] = tokensEntity.refreshTokenExpiresIn
        }
        val foundToken = Tokens.select { Tokens.userId.eq(tokensEntity.userId) }.map(::rowToTokens).singleOrNull()
        return if (foundToken == tokensEntity) {
            foundToken
        } else null
    }

    override suspend fun deleteTokens(userId: Long): Boolean = dbQuery {
        Tokens.deleteWhere { Tokens.userId.eq(userId) } == 1
    }
}

fun rowToTokens(row: ResultRow) = TokensEntity(
    userId = row[Tokens.userId],
    accessToken = row[Tokens.accessToken],
    refreshToken = row[Tokens.refreshToken],
    accessTokenExpiresIn = row[Tokens.accessTokenExpiresIn],
    refreshTokenExpiresIn = row[Tokens.refreshTokenExpiresIn]
)