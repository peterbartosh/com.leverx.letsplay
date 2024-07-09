package plugins

import data.service.AuthService
import data.service.JwtService
import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureAuth(authService: AuthService){
    install(Authentication) {
        bearer("auth-bearer") {
            authenticate { tokenCredential ->
                authService.validateAccessToken(tokenCredential)
            }
        }
    }
}