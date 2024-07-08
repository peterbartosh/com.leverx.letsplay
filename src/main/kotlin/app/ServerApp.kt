package app

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ServerApp : KoinComponent {
    private val api: Api by inject()
    fun start(){ api.startEmbeddedServer() }
}