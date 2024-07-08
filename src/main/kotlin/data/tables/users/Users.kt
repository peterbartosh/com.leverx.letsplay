package data.tables.users

import data.model.basic.Skill
import org.jetbrains.exposed.sql.Table

object Users : Table() {

    val id = long("id").autoIncrement()
    val name = varchar("location", 128)
    val age = integer("age")
    val rating = float("rating")
    val skills = varchar("skills", 128)

    override val primaryKey = PrimaryKey(id)
}