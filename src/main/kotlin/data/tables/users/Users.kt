package data.tables.users

import org.jetbrains.exposed.sql.Table

object Users : Table() {

    val id = long("id").autoIncrement()
    val username = varchar("username", 128).uniqueIndex()
    val email = varchar("email",24).uniqueIndex()
    val password = varchar("password",24)
    val age = integer("age").nullable()
    val rating = float("rating").nullable()
    val skills = varchar("skills", 128).nullable()

    override val primaryKey = PrimaryKey(id)
}