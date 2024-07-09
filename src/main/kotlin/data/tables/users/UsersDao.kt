package data.tables.users

import com.auth0.jwt.interfaces.DecodedJWT
import model.entity.UserEntity

interface UsersDao {

    suspend fun getUser(userId: Long): UserEntity?

    suspend fun getUserByUsername(username: String): UserEntity?

    suspend fun getUserByEmail(email: String): UserEntity?

    suspend fun addUser(userEntity: UserEntity, copyId: Boolean = false): UserEntity?

    suspend fun getUserByRefreshToken(refreshToken: DecodedJWT): UserEntity?

    suspend fun clearAll()

}