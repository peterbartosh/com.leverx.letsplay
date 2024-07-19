package data.service

import data.converter.toDto
import data.tables.tokens.TokensDao
import data.tables.users.UsersDao
import data.utils.Utils
import io.ktor.http.*
import io.ktor.server.auth.*
import model.basic.Response
import model.entity.TokensEntity
import model.entity.UserEntity
import model.request.SignInRequest
import model.request.SignUpRequest
import model.response.LoginResponse
import model.response.User
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class AuthService(
    private val jwtService: JwtService,
    private val tokensDao: TokensDao,
    private val usersDao: UsersDao
) {

    // refresh token is expired
    suspend fun signIn(signInRequest: SignInRequest): Response<LoginResponse> = newSuspendedTransaction {

        val isEmailProvided = Utils.isEmailValid(signInRequest.usernameOrEmail)

        val foundUserResult = if (isEmailProvided) {
            val email = signInRequest.usernameOrEmail
            usersDao.getUserByEmail(email)
        } else {
            val username = signInRequest.usernameOrEmail
            usersDao.getUserByUsername(username)
        }
        val foundUser = foundUserResult.getOrNull()

        if (foundUserResult.isFailure || foundUser == null) {
            this.rollback()
            return@newSuspendedTransaction Response.Error(
                statusCode = HttpStatusCode.BadRequest,
                message = "User not found"
            )
        }

        if (foundUser.password != signInRequest.password) {
            this.rollback()
            return@newSuspendedTransaction Response.Error(
                statusCode = HttpStatusCode.BadRequest,
                message = "Password is incorrect"
            )
        }

        val updatedTokensResult = tokensDao.editTokens(
            TokensEntity(
                userId = foundUser.id,
                accessToken = jwtService.createAccessToken(),
                refreshToken = jwtService.createRefreshToken(),
                accessTokenExpiresIn = jwtService.accessTokenExpiresIn(),
                refreshTokenExpiresIn = jwtService.refreshTokenExpiresIn()
            )
        )
        val updatedTokens = updatedTokensResult.getOrNull()

        if (updatedTokensResult.isFailure || updatedTokens == null) {
            this.rollback()
            return@newSuspendedTransaction Response.Error(
                statusCode = HttpStatusCode.InternalServerError
            )
        }

        return@newSuspendedTransaction Response.Data(
            body = LoginResponse(
                accessToken = updatedTokens.accessToken,
                refreshToken = updatedTokens.refreshToken,
                accessTokenExpiresIn = updatedTokens.accessTokenExpiresIn,
                refreshTokenExpiresIn = updatedTokens.refreshTokenExpiresIn
            )
        )
    }

    // new user
    suspend fun signUp(signUpRequest: SignUpRequest): Response<LoginResponse> =
        newSuspendedTransaction {
            val insertedUserResult = usersDao.addUser(
                userEntity = UserEntity(
                    id = 0L,
                    username = signUpRequest.username,
                    password = signUpRequest.password,
                    email = signUpRequest.email,
                    age = signUpRequest.age
                )
            )
            val insertedUser = insertedUserResult.getOrNull()

            if (insertedUserResult.isFailure || insertedUser == null) {
                this.rollback()
                return@newSuspendedTransaction Response.Error(
                    statusCode = HttpStatusCode.BadRequest,
                    message = "Credentials are used"
                )
            }

            val insertedUsersTokensResult = tokensDao.addTokens(
                tokensEntity = TokensEntity(
                    userId = insertedUser.id,
                    accessToken = jwtService.createAccessToken(),
                    refreshToken = jwtService.createRefreshToken(),
                    accessTokenExpiresIn = jwtService.accessTokenExpiresIn(),
                    refreshTokenExpiresIn = jwtService.refreshTokenExpiresIn()
                )
            )
            val insertedUsersTokens = insertedUsersTokensResult.getOrNull()

            if (insertedUsersTokensResult.isFailure || insertedUsersTokens == null) {
                this.rollback()
                return@newSuspendedTransaction Response.Error(
                    statusCode = HttpStatusCode.InternalServerError
                )
            }

            return@newSuspendedTransaction Response.Data(
                body = LoginResponse(
                    accessToken = insertedUsersTokens.accessToken,
                    refreshToken = insertedUsersTokens.refreshToken,
                    accessTokenExpiresIn = insertedUsersTokens.accessTokenExpiresIn,
                    refreshTokenExpiresIn = insertedUsersTokens.refreshTokenExpiresIn
                )
            )
        }

    suspend fun logout(accessToken: String): Response<Any> {
        val foundUserTokenResult = tokensDao.getTokensByAccessToken(accessToken)
        val foundUserToken = foundUserTokenResult.getOrNull()
        if (foundUserTokenResult.isFailure || foundUserToken == null) {
            return Response.Error(
                statusCode = HttpStatusCode.Unauthorized
            )
        }
        val deleteResult = tokensDao.deleteTokens(foundUserToken.userId)
        if (deleteResult.isFailure || deleteResult.getOrNull() == null) {
            return Response.Error(
                statusCode = HttpStatusCode.InternalServerError
            )
        }
        return Response.Data(body = Any())
    }

    suspend fun validateAccessToken(credential: BearerTokenCredential): Principal? {
        val tokensResult = tokensDao.getTokensByAccessToken(credential.token)
        val tokens = tokensResult.getOrNull()

        if (tokensResult.isFailure || tokens == null) {
            return null
        }

        val accessTokenIsExpired = System.currentTimeMillis() >= tokens.accessTokenExpiresIn

        return if (accessTokenIsExpired) {
            null
        } else {
            UserIdPrincipal(name = "princ")
        }
    }

    suspend fun refreshToken(refreshToken: String): Response<LoginResponse> {
        val tokensResult = tokensDao.getTokensByRefreshToken(refreshToken)
        val tokens = tokensResult.getOrNull()

        if (tokensResult.isFailure || tokens == null) {
            return Response.Error(
                statusCode = HttpStatusCode.BadRequest,
                message = "Not found"
            )
        }

        val refreshTokenIsExpired = System.currentTimeMillis() >= tokens.refreshTokenExpiresIn

        return if (refreshTokenIsExpired) {
            Response.Error(statusCode = HttpStatusCode.Unauthorized, message = "Reathorization required")
        } else {
            val tokensEntity = TokensEntity(
                userId = tokens.userId,
                accessToken = jwtService.createAccessToken(),
                accessTokenExpiresIn = jwtService.accessTokenExpiresIn(),
                refreshToken = jwtService.createRefreshToken(),
                refreshTokenExpiresIn = jwtService.refreshTokenExpiresIn()
            )
            val updatedTokensResult = tokensDao.editTokens(tokensEntity = tokensEntity)
            val updatedTokens = updatedTokensResult.getOrNull()

            if (updatedTokensResult.isFailure || updatedTokens == null) {
                Response.Error(statusCode = HttpStatusCode.InternalServerError)
            } else {
                Response.Data(
                    body = LoginResponse(
                        accessToken = updatedTokens.accessToken,
                        refreshToken = updatedTokens.refreshToken,
                        accessTokenExpiresIn = updatedTokens.accessTokenExpiresIn,
                        refreshTokenExpiresIn = updatedTokens.refreshTokenExpiresIn
                    )
                )
            }
        }
    }

    suspend fun getUser(accessToken: String): Response<User> = newSuspendedTransaction {
        val tokensResult = tokensDao.getTokensByAccessToken(accessToken)
        val tokens = tokensResult.getOrNull()

        if (tokensResult.isFailure || tokens == null) {
            this.rollback()
            return@newSuspendedTransaction Response.Error(statusCode = HttpStatusCode.Unauthorized)
        }
        val userResult = usersDao.getUser(tokens.userId)
        val user = userResult.getOrNull()

        if (userResult.isFailure || user == null) {
            this.rollback()
            return@newSuspendedTransaction Response.Error(statusCode = HttpStatusCode.NotFound)
        }
        return@newSuspendedTransaction Response.Data(
            body = user.toDto()
        )
    }

}