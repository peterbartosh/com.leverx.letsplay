package app

import di.databaseModule
import di.networkModule
import org.koin.core.context.startKoin

fun main(args: Array<String>) {
    startKoin {
        printLogger()
        modules(databaseModule, networkModule)
    }
    ServerApp().start()
}