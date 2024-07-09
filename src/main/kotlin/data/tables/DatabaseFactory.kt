package data.tables

import data.tables.events.Events
import data.tables.events_users.EventsUsers
import data.tables.locations.Locations
import data.tables.tokens.Tokens
import data.tables.users.Users
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val database = Database.connect(
            url = "jdbc:postgresql://localhost:5432/letsplay",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "peter_leverx5431"
        )
        transaction(database) {
            SchemaUtils.create(Events, Locations, Users, EventsUsers, Tokens)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}