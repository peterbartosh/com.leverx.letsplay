package data.tables.users

import model.entity.UserEntity

interface UsersDao {

    suspend fun getUser(userId: Long): Result<UserEntity?>

    suspend fun getUserByUsername(username: String): Result<UserEntity?>

    suspend fun getUserByEmail(email: String): Result<UserEntity?>

    suspend fun addUser(userEntity: UserEntity): Result<UserEntity?>

    suspend fun clearAll(): Result<Int>

}