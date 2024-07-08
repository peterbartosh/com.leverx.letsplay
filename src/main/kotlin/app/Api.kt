package app

import app.init.InitDatabase
import data.tables.DatabaseFactory
import app.plugins.*
import data.source.RemoteSource
import data.tables.events.Events
import data.tables.locations.Locations
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent

class Api(
    private val remoteSource: RemoteSource,
    private val initDatabase: InitDatabase
) : KoinComponent {

    fun startEmbeddedServer() {

        val port = (System.getenv("PORT") ?: "8080").toInt()

        embeddedServer(factory = Netty, port = port) {
            configureSerialization()
            configureMonitoring()
            configureHTTP()
            configureSecurity()

            initDatabase()

            configureRouting(remoteSource)

        }.start(wait = true)
    }
}