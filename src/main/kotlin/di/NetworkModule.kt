package di

import app.Api
import app.init.InitDatabase
import app.init.InitDatabaseImpl
import data.source.RemoteSource
import org.koin.dsl.module

val networkModule = module {
    single {
        RemoteSource(
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
            eventsUsersDao = get()
        )
    }
    single { Api(remoteSource = get(), initDatabase = get()) }
}