package plugins.routing.components

import data.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.basic.Response
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
                    val isSuccess = authService.logout(accessToken)
                    call.respond(HttpStatusCode.OK, isSuccess)
                } ?: call.respond(HttpStatusCode.Forbidden, false)
            }
        }
        authenticate(Routes.Auth.authBearer) {
            post(Routes.refreshToken) {
                call.getTokenFromHeader()?.let { oldAccessToken ->
                    call.respond(authService.refreshToken(oldAccessToken = oldAccessToken))
                }
            }
        }

    }
}