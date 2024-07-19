package app.init

import data.converter.convertSkills
import data.service.AuthService
import data.tables.DatabaseFactory.dbQuery
import data.tables.users.UsersDao
import model.basic.Skill
import model.entity.UserEntity
import model.request.SignUpRequest

internal suspend fun AuthService.addSomeData() = dbQuery {
    this@addSomeData.signUp(
        SignUpRequest(
            username = "Peter",
            password = "13214",
            email = "pe.fwo@gmail.com",
            age = 21
        )
    )
    this@addSomeData.signUp(
        SignUpRequest(
            username = "Petro5",
            password = "petro3",
            email = "petya.moc@gmail.com",
            age = 24
        )
    )
    this@addSomeData.signUp(
        SignUpRequest(
            username = "Oleg",
            password = "3ru3290r",
            email = "olezha.fwo@gmail.com",
            age = 25,
        )
    )
    this@addSomeData.signUp(
        SignUpRequest(
            username = "volodya",
            password = "volodya",
            email = "volodya.vologda@gmail.com",
            age = 28,
        )
    )
}