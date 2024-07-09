package app

import app.init.InitDatabase
import data.service.AuthService
import data.service.ClientService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.core.component.KoinComponent
import plugins.configureAuth
import plugins.configureMonitoring
import plugins.routing.configureRouting
import plugins.configureSerialization

class Api(
    private val clientService: ClientService,
    private val authService: AuthService,
    private val initDatabase: InitDatabase
) : KoinComponent {

    fun startEmbeddedServer(): NettyApplicationEngine {

        val port = (System.getenv("PORT") ?: "8080").toInt()

        return embeddedServer(factory = Netty, port = port) {
            configureSerialization()
            configureMonitoring()
            configureAuth(authService)

            initDatabase()

            configureRouting(clientService, authService)

        }.start(wait = true)
    }
}