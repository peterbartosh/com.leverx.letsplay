package data.tables.users

import data.tables.DatabaseFactory.dbQuery
import data.model.entity.UserEntity
import org.jetbrains.exposed.sql.*

class UsersDaoImpl: UsersDao {

    override suspend fun getUser(userId: Long) = dbQuery {
        Users.select {
            Users.id eq userId
        }.map(::rowToUser).singleOrNull()
    }

    override suspend fun addUser(userEntity: UserEntity) {
        Users.insert {
            it[Users.id] = userEntity.id
            it[Users.age] = userEntity.age
            it[Users.name] = userEntity.name
            it[Users.rating] = userEntity.rating
            it[Users.skills] = userEntity.skills
        }
    }

    override suspend fun clearAll() {
        Users.deleteAll()
    }

    private fun rowToUser(row: ResultRow) = UserEntity(
        id = row[Users.id],
        name = row[Users.name],
        age = row[Users.age],
        rating = row[Users.rating],
        skills = row[Users.skills]
    )

}