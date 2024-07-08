package data.tables.events_users

import data.tables.events.Events
import data.tables.users.Users
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import java.sql.Ref

object EventsUsers : Table() {

    val id = long("id").autoIncrement()
    val eventId = reference(
        name = "event_id",
        refColumn = Events.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.NO_ACTION
    )
    val userId = reference(
        name = "user_id",
        refColumn = Users.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.NO_ACTION
    )
    val isAdmin = bool("is_admin")

    override val primaryKey = PrimaryKey(id)
    override val tableName: String
        get() = "events_users"

}