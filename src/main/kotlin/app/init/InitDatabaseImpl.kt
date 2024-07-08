package app.init

import data.tables.DatabaseFactory
import data.tables.events.Events
import data.tables.events.EventsDao
import data.tables.events_users.EventsUsersDao
import data.tables.locations.Locations
import data.tables.locations.LocationsDao
import data.tables.users.UsersDao
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent

class InitDatabaseImpl(
    private val usersDao: UsersDao,
    private val locationsDao: LocationsDao,
    private val eventsDao: EventsDao,
    private val eventsUsersDao: EventsUsersDao
) : InitDatabase, KoinComponent {

    override operator fun invoke(
        addDefaultData: Boolean,
        clearExistingData: Boolean
    ) {

        DatabaseFactory.init()

        if (addDefaultData) {

            val coroutineContext = Dispatchers.Default + Job() + CoroutineExceptionHandler { _, throwable ->
                throwable.printStackTrace()
            }

            CoroutineScope(coroutineContext).launch {
                if (clearExistingData) {
                    launch(coroutineContext) {
                        newSuspendedTransaction {
                            eventsDao.clearAll()
                            usersDao.clearAll()
                            locationsDao.clearAll()
                        }
                    }.join()
                }

                launch(coroutineContext) {
                    newSuspendedTransaction {
                        usersDao.addSomeData()
                        locationsDao.addSomeData()
                    }
                }.join()

                launch(coroutineContext) {
                    newSuspendedTransaction {
                        eventsDao.addSomeData()
                        eventsUsersDao.addSomeData()
                    }
                }.join()

            }
        }
    }
}