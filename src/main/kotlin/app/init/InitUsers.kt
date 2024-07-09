package app.init

import data.converter.convertSkills
import data.tables.users.UsersDao
import model.basic.Skill
import model.entity.UserEntity

suspend fun UsersDao.addSomeData() {
    this.addUser(
        UserEntity(
            id = 1L,
            username = "Peter",
            password = "13214",
            email = "pe.fwo@gmail.com",
            age = 21,
            rating = 4.3f,
            skills = convertSkills(
                listOf(Skill(sportType = 1, skillLevel = 4), Skill(sportType = 2, skillLevel = 3))
            )
        ),
        copyId = true
    )
    this.addUser(
        UserEntity(
            id = 2L,
            username = "Oleg",
            password = "3ru3290r",
            email = "olezha.fwo@gmail.com",
            age = 25,
            rating = 4.7f,
            skills = convertSkills(
                listOf(Skill(sportType = 1, skillLevel = 4))
            )
        ),
        copyId = true
    )
    this.addUser(
        UserEntity(
            id = 3L,
            username = "Varvara",
            password = "jdwiery8",
            email = "vrvr.fwo@gmail.com",
            age = 27,
            rating = 4.9f,
            skills = convertSkills(
                listOf(
                    Skill(sportType = 1, skillLevel = 3),
                    Skill(sportType = 2, skillLevel = 2),
                    Skill(sportType = 3, skillLevel = 4)
                )
            )
        ),
        copyId = true
    )
}