package data.tables.users

import com.auth0.jwt.interfaces.DecodedJWT
import model.entity.UserEntity

interface UsersDao {

    suspend fun getUser(userId: Long): Result<UserEntity?>

    suspend fun getUserByUsername(username: String): Result<UserEntity?>

    suspend fun getUserByEmail(email: String): Result<UserEntity?>

    suspend fun addUser(userEntity: UserEntity, copyId: Boolean = false): Result<UserEntity?>

    suspend fun clearAll(): Result<Int>

}