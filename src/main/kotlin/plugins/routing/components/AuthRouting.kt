package plugins.routing.components

import data.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.request.SignInRequest
import model.request.SignUpRequest
import plugins.routing.Routes
import plugins.routing.getTokenFromHeader
import plugins.routing.respond

fun Application.configureAuthRouting(authService: AuthService){

    routing {

        post(Routes.signIn) {
            val signInRequest = call.receive<SignInRequest>()
            call.respond(authService.signIn(signInRequest))
        }

        post(Routes.signUp) {
            val signUpRequest = call.receive<SignUpRequest>()
            call.respond(authService.signUp(signUpRequest))
        }

        authenticate(Routes.Auth.authBearer) {
            post(Routes.logout) {
                call.getTokenFromHeader()?.let { accessToken ->
                    val isSuccessResponse = authService.logout(accessToken)
                    call.respond(isSuccessResponse)
                } ?: call.respond(HttpStatusCode.Forbidden, false)
            }
        }
        authenticate(Routes.Auth.authBearer) {
            get(Routes.refreshToken) {
                val refreshToken = call.request.queryParameters["refresh_token"]
                if (refreshToken.isNullOrBlank()) {
                    call.respond(status = HttpStatusCode.BadRequest, "")
                } else {
                    call.respond(authService.refreshToken(refreshToken = refreshToken))
                }
            }
        }
        authenticate(Routes.Auth.authBearer) {
            get(Routes.getAuthenticatedUser) {
                call.getTokenFromHeader()?.let { accessToken ->
                    val userResponse = authService.getUser(accessToken = accessToken)
                    call.respond(userResponse)
                } ?: call.respond(HttpStatusCode.Forbidden, false)
            }
        }
    }
}