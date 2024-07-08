package data.tables.users

import model.entity.UserEntity

interface UsersDao {

    suspend fun getUser(userId: Long): UserEntity?

    suspend fun addUser(userEntity: UserEntity)

    suspend fun clearAll()

}