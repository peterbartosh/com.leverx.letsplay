package data.tables.users

import org.jetbrains.exposed.sql.Table

val usersAutoIncSeqName = "user_id_seq"

object Users : Table() {

    val id = long("id").autoIncrement(usersAutoIncSeqName)
    val username = varchar("username", 128).uniqueIndex()
    val email = varchar("email",36).uniqueIndex()
    val password = varchar("password",26)
    val age = integer("age").nullable()
    val rating = float("rating").nullable()
    val skills = varchar("skills", 128).nullable()

    override val primaryKey = PrimaryKey(id)
}