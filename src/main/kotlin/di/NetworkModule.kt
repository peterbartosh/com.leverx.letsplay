package di

import app.Api
import app.init.InitDatabase
import app.init.InitDatabaseImpl
import data.service.AuthService
import data.service.ClientService
import data.service.JwtService
import io.ktor.server.application.*
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.math.sin

val networkModule = module {
    single {
        JwtService()
    }
    single {
        AuthService(
            jwtService = get(),
            tokensDao = get(),
            usersDao = get()
        )
    }
    single {
        ClientService(
            locationsDao = get(),
            usersDao = get(),
            eventsUsersDao = get(),
            eventsDao = get()
        )
    }
    single<InitDatabase> {
        InitDatabaseImpl(
            usersDao = get(),
            locationsDao = get(),
            eventsDao = get(),
            eventsUsersDao = get(),
            tokensDao = get(),
            authService = get(),
            clientService = get()
        )
    }
    single {
        Api(
            clientService = get(),
            authService = get(),
            initDatabase = get()
        )
    }
}