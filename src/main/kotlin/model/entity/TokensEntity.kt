package model.entity

import data.tables.tokens.Tokens.long
import data.tables.tokens.Tokens.reference
import data.tables.tokens.Tokens.varchar
import data.tables.users.Users

data class TokensEntity(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresIn: Long,
    val refreshTokenExpiresIn: Long
)
