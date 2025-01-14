package plugins.routing.components

import data.service.ClientService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import plugins.routing.Routes
import plugins.routing.respondToCall


fun Application.configureUsersRouting(
    clientService: ClientService
) {
    routing {
        authenticate(Routes.Auth.authBearer) {
            get(Routes.getUser) {
                val userId = call.parameters["user_id"]?.toLong()
                if (userId == null) {
                    call.respond("Failure. User not found")
                } else {
                    val response = clientService.getUser(userId)
                    call.respondToCall(response)
                }
            }
        }
    }
}