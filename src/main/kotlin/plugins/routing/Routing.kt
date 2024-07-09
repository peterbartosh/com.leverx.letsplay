package plugins.routing

import data.service.AuthService
import data.service.ClientService
import io.ktor.server.application.*
import plugins.routing.components.configureAuthRouting
import plugins.routing.components.configureEventsRouting
import plugins.routing.components.configureLocationsRouting
import plugins.routing.components.configureUsersRouting

fun Application.configureRouting(
    clientService: ClientService,
    authService: AuthService
) {
    configureEventsRouting(clientService)
    configureUsersRouting(clientService)
    configureLocationsRouting(clientService)
    configureAuthRouting(authService)
}

