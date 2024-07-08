package app.init

import data.tables.DatabaseFactory
import data.tables.events.EventsDao
import data.tables.events_users.EventsUsersDao
import data.tables.locations.LocationsDao
import data.tables.users.UsersDao
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent

interface InitDatabase {
    operator fun invoke(addDefaultData: Boolean = false, clearExistingData: Boolean = true)
}

