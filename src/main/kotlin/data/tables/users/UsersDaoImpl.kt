package data.tables.users

import com.auth0.jwt.interfaces.DecodedJWT
import data.tables.DatabaseFactory.dbQuery
import data.tables.tokens.Tokens
import data.tables.tokens.rowToTokens
import model.entity.UserEntity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.postgresql.util.PSQLException

class UsersDaoImpl : UsersDao {

    override suspend fun getUser(userId: Long) = dbQuery {
        Users.select {
            Users.id eq userId
        }.map(::rowToUser).singleOrNull()
    }

    override suspend fun getUserByUsername(username: String): UserEntity? = dbQuery {
        Users.select { Users.username eq username }.map(::rowToUser).singleOrNull()
    }

    override suspend fun getUserByEmail(email: String): UserEntity? = dbQuery {
        Users.select { Users.email eq email }.map(::rowToUser).singleOrNull()
    }

    override suspend fun getUserByRefreshToken(refreshToken: DecodedJWT): UserEntity? {
        return Tokens
            .select {
                Tokens.refreshToken.eq(refreshToken.token)
            }.map(::rowToTokens).singleOrNull()?.let { tokens ->
                Users.select {
                    Users.id.eq(tokens.userId)
                }.map(::rowToUser).singleOrNull()
            }
    }

    override suspend fun addUser(userEntity: UserEntity, copyId: Boolean): UserEntity? {
        println("1111111111111111111")
        return try {
            newSuspendedTransaction {
                Users.insert {
                    if (copyId) {
                        it[id] = userEntity.id
                    }
                    it[age] = userEntity.age
                    it[username] = userEntity.username
                    it[email] = userEntity.email
                    it[password] = userEntity.password
                    it[rating] = userEntity.rating
                    it[skills] = userEntity.skills
                }
                println("222222222222222222222")
                val foundUsers = Users.select { Users.username.eq(userEntity.username) }.map(::rowToUser)
                println("33333333333333333333333")
                if (foundUsers.size == 1) {
                    foundUsers.first()
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun clearAll() {
        Users.deleteAll()
    }

}

fun rowToUser(row: ResultRow) = UserEntity(
    id = row[Users.id],
    username = row[Users.username],
    password = row[Users.password],
    email = row[Users.email],
    age = row[Users.age],
    rating = row[Users.rating],
    skills = row[Users.skills]
)