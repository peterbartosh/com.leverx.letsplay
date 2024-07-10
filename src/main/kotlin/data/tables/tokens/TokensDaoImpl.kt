package data.tables.tokens

import data.tables.DatabaseFactory.dbQuery
import model.entity.TokensEntity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class TokensDaoImpl: TokensDao {

    override suspend fun getTokensByAccessToken(accessToken: String) = dbQuery {
        Tokens.select {
            Tokens.accessToken.eq(accessToken)
        }.map(::rowToTokens).singleOrNull()
    }

    override suspend fun getTokensByRefreshToken(refreshToken: String) = dbQuery {
        Tokens.select {
            Tokens.refreshToken.eq(refreshToken)
        }.map(::rowToTokens).singleOrNull()
    }

    override suspend fun getTokensByUserId(userId: Long) = dbQuery {
        Tokens.select {
            Tokens.userId.eq(userId)
        }.map(::rowToTokens).singleOrNull()
    }

    override suspend fun addTokens(tokensEntity: TokensEntity) = dbQuery {
        Tokens.insert {
            try {
                it[userId] = tokensEntity.userId
                it[accessToken] = tokensEntity.accessToken
                it[refreshToken] = tokensEntity.refreshToken
                it[accessTokenExpiresIn] = tokensEntity.accessTokenExpiresIn
                it[refreshTokenExpiresIn] = tokensEntity.refreshTokenExpiresIn
            } catch (e: Exception) {
                this@dbQuery.rollback()
                e.printStackTrace()
            }
        }
        val foundTokens = Tokens.select { Tokens.userId.eq(tokensEntity.userId) }.map(::rowToTokens)
        if (foundTokens.size == 1) foundTokens.first() else null
    }

    override suspend fun editTokens(tokensEntity: TokensEntity) = dbQuery {
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
        if (foundToken == tokensEntity) {
            foundToken
        } else null
    }

    override suspend fun deleteTokens(userId: Long) = dbQuery {
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