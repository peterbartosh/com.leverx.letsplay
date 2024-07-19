package app.init

import data.service.AuthService
import data.service.ClientService
import data.service.JwtService
import data.tables.DatabaseFactory
import data.tables.events.Events
import data.tables.events.EventsDao
import data.tables.events.eventsAutoIncSeqName
import data.tables.events_users.EventsUsers
import data.tables.events_users.EventsUsersDao
import data.tables.events_users.eventsUsersAutoIncSeqName
import data.tables.locations.Locations
import data.tables.locations.LocationsDao
import data.tables.tokens.Tokens
import data.tables.tokens.TokensDao
import data.tables.users.Users
import data.tables.users.UsersDao
import data.tables.users.usersAutoIncSeqName
import kotlinx.coroutines.*
import model.request.SignUpRequest
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent

class InitDatabaseImpl(
    private val usersDao: UsersDao,
    private val locationsDao: LocationsDao,
    private val eventsDao: EventsDao,
    private val eventsUsersDao: EventsUsersDao,
    private val tokensDao: TokensDao,
    private val clientService: ClientService,
    private val authService: AuthService
) : InitDatabase, KoinComponent {

    private suspend fun resetAutoIncSequences() = newSuspendedTransaction {

        val minValue = 1
        val connection = TransactionManager.current().connection
        connection.executeInBatch(
            listOf(
                "ALTER SEQUENCE $eventsAutoIncSeqName RESTART WITH $minValue",
                "ALTER SEQUENCE $eventsUsersAutoIncSeqName RESTART WITH $minValue",
                "ALTER SEQUENCE $usersAutoIncSeqName RESTART WITH $minValue",
            )
        )

    }

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
                            launch {
                                tokensDao.clearAll()
                            }.join()
                            launch {
                                eventsDao.clearAll()
                            }.join()
                            launch {
                                eventsUsersDao.clearAll()
                            }.join()
                            launch {
                                usersDao.clearAll()
                            }.join()
                            launch {
                                locationsDao.clearAll()
                            }.join()
                        }
                    }.join()
                }

                launch(coroutineContext) {
                    newSuspendedTransaction {
                        launch {
                            resetAutoIncSequences()
                        }.join()
                        launch {
                            authService.addSomeData()
                        }.join()
                        launch {
                            clientService.addSomeData()
                        }.join()
                    }
                }.join()
            }
        }
    }
}