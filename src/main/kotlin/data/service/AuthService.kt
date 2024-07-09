package data.service

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
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class AuthService(
    private val jwtService: JwtService,
    private val tokensDao: TokensDao,
    private val usersDao: UsersDao
) {

    // refresh token is expired
    suspend fun signIn(signInRequest: SignInRequest): Response<LoginResponse> = newSuspendedTransaction {

        val isEmailProvided = Utils.isEmailValid(signInRequest.usernameOrEmail)

        val foundUser = if (isEmailProvided) {
            val email = signInRequest.usernameOrEmail
            usersDao.getUserByEmail(email)
        } else {
            val username = signInRequest.usernameOrEmail
            usersDao.getUserByUsername(username)
        } ?: return@newSuspendedTransaction Response.Error(
            statusCode = HttpStatusCode.BadRequest,
            message = "User not found"
        )

        if (foundUser.password == signInRequest.password) {
            return@newSuspendedTransaction Response.Error(
                statusCode = HttpStatusCode.BadRequest,
                message = "Password is incorrect"
            )
        }

        val updatedTokens = tokensDao.editTokens(
            TokensEntity(
                userId = foundUser.id,
                accessToken = jwtService.createAccessToken(),
                refreshToken = jwtService.createRefreshToken(),
                accessTokenExpiresIn = jwtService.accessTokenExpiresIn(),
                refreshTokenExpiresIn = jwtService.refreshTokenExpiresIn()
            )
        ) ?: return@newSuspendedTransaction Response.Error(statusCode = HttpStatusCode.InternalServerError)

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
    suspend fun signUp(signUpRequest: SignUpRequest): Response<LoginResponse> = newSuspendedTransaction {
        val insertedUser = usersDao.addUser(
            userEntity = UserEntity(
                id = 0L,
                username = signUpRequest.username,
                password = signUpRequest.password,
                email = signUpRequest.email,
                age = signUpRequest.age
            )
        ) ?: return@newSuspendedTransaction Response.Error(
            statusCode = HttpStatusCode.BadRequest,
            message = "Credentials are used"
        )
        println("2")

        val insertedUsersCred = tokensDao.addTokens(
            tokensEntity = TokensEntity(
                userId = insertedUser.id,
                accessToken = jwtService.createAccessToken(),
                refreshToken = jwtService.createRefreshToken(),
                accessTokenExpiresIn = jwtService.accessTokenExpiresIn(),
                refreshTokenExpiresIn = jwtService.refreshTokenExpiresIn()
            )
        ) ?: return@newSuspendedTransaction Response.Error(statusCode = HttpStatusCode.InternalServerError)
        println("3")

        return@newSuspendedTransaction Response.Data(
            body = LoginResponse(
                accessToken = insertedUsersCred.accessToken,
                refreshToken = insertedUsersCred.refreshToken,
                accessTokenExpiresIn = insertedUsersCred.accessTokenExpiresIn,
                refreshTokenExpiresIn = insertedUsersCred.refreshTokenExpiresIn
            )
        )
    }

    suspend fun logout(accessToken: String): Boolean {
        return tokensDao.getTokensByAccessToken(accessToken)?.let { foundUserToken ->
            tokensDao.deleteTokens(foundUserToken.userId)
        } ?: false
    }

    suspend fun validateAccessToken(
        credential: BearerTokenCredential,
    ): Principal? {
        val tokens = tokensDao.getTokensByAccessToken(credential.token) ?: return null
        val accessTokenIsExpired = System.currentTimeMillis() >= tokens.accessTokenExpiresIn

        return if (accessTokenIsExpired) {
            null
        } else {
            UserIdPrincipal(name = "princ")
        }
    }

    suspend fun refreshToken(oldAccessToken: String): Response<LoginResponse> {
        val tokens = tokensDao.getTokensByAccessToken(oldAccessToken) ?: return Response.Error(
            statusCode = HttpStatusCode.BadRequest,
            message = "Not found"
        )

        val refreshTokenIsExpired = System.currentTimeMillis() >= tokens.refreshTokenExpiresIn

        return if (refreshTokenIsExpired) {
            Response.Error(statusCode = HttpStatusCode.Unauthorized, message = "Reathorization required")
        } else {
            val tokensEntity = tokens.copy(
                accessToken = jwtService.createAccessToken(),
                accessTokenExpiresIn = jwtService.accessTokenExpiresIn()
            )
            tokensDao.editTokens(tokensEntity = tokensEntity)?.let { updatedTokens ->
                Response.Data(
                    body = LoginResponse(
                        accessToken = updatedTokens.accessToken,
                        refreshToken = updatedTokens.refreshToken,
                        accessTokenExpiresIn = updatedTokens.accessTokenExpiresIn,
                        refreshTokenExpiresIn = updatedTokens.refreshTokenExpiresIn
                    )
                )
            } ?:Response.Error(statusCode = HttpStatusCode.InternalServerError)
        }
    }

}