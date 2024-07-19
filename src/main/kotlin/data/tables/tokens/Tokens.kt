package data.tables.tokens

import data.tables.users.Users
import org.jetbrains.exposed.sql.Table

object Tokens: Table() {
    val userId = reference("user_id", Users.id)
    val accessToken = varchar("access_token", 512)
    val refreshToken = varchar("refresh_token", 512)
    val accessTokenExpiresIn = long("access_token_expires_in")
    val refreshTokenExpiresIn = long("refresh_token_expires_in")

    override val primaryKey = PrimaryKey(userId)
}