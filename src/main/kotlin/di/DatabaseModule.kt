package di

import data.tables.events.EventsDao
import data.tables.events.EventsDaoImpl
import data.tables.events_users.EventsUsersDao
import data.tables.events_users.EventsUsersDaoImpl
import data.tables.locations.LocationsDao
import data.tables.locations.LocationsDaoImpl
import data.tables.users.UsersDao
import data.tables.users.UsersDaoImpl
import org.koin.dsl.module
import kotlin.math.sin

val databaseModule = module {
    single<UsersDao> { UsersDaoImpl() }
    single<LocationsDao> { LocationsDaoImpl() }
    single<EventsDao> { EventsDaoImpl(eventsUsersDao = get()) }
    single<EventsUsersDao> { EventsUsersDaoImpl() }
}