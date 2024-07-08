package app

import app.init.InitDatabase
import data.source.RemoteSource
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.core.component.KoinComponent
import plugins.configureMonitoring
import plugins.configureRouting
import plugins.configureSerialization

class Api(
    private val remoteSource: RemoteSource,
    private val initDatabase: InitDatabase
) : KoinComponent {

    fun startEmbeddedServer() {

        val port = (System.getenv("PORT") ?: "8080").toInt()

        embeddedServer(factory = Netty, port = port) {
            configureSerialization()
            configureMonitoring()

            initDatabase()

            configureRouting(remoteSource)

        }.start(wait = true)
    }
}