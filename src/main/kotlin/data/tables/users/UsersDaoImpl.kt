package data.tables.users

import data.tables.DatabaseFactory.dbQuery
import model.entity.UserEntity
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class UsersDaoImpl : UsersDao {

    override suspend fun getUser(userId: Long) = dbQuery {
        Users.select {
            Users.id eq userId
        }.map(::rowToUser).singleOrNull()
    }

    override suspend fun getUserByUsername(username: String) = dbQuery {
        Users.select { Users.username eq username }.map(::rowToUser).singleOrNull()
    }

    override suspend fun getUserByEmail(email: String) = dbQuery {
        Users.select { Users.email eq email }.map(::rowToUser).singleOrNull()
    }

    override suspend fun addUser(userEntity: UserEntity) = dbQuery {
        Users.insert {
            it[age] = userEntity.age
            it[username] = userEntity.username
            it[email] = userEntity.email
            it[password] = userEntity.password
            it[rating] = userEntity.rating
            it[skills] = userEntity.skills
        }.resultedValues?.map(::rowToUser)?.singleOrNull()
    }


    override suspend fun clearAll() = dbQuery {
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