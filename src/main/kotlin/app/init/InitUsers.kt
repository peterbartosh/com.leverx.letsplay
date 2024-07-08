package app.init

import data.converter.convertSkills
import data.tables.users.UsersDao
import data.model.basic.Skill
import data.model.entity.UserEntity

suspend fun UsersDao.addSomeData() {
    this.addUser(
        UserEntity(
            id = 1L,
            name = "Peter",
            age = 21,
            rating = 4.3f,
            skills = convertSkills(
                listOf(Skill(sportType = 1, skillLevel = 4), Skill(sportType = 2, skillLevel = 3))
            )
        )
    )
    this.addUser(
        UserEntity(
            id = 2L,
            name = "Oleg",
            age = 25,
            rating = 4.7f,
            skills = convertSkills(
                listOf(Skill(sportType = 1, skillLevel = 4))
            )
        )
    )
    this.addUser(
        UserEntity(
            id = 3L,
            name = "Varvara",
            age = 27,
            rating = 4.9f,
            skills = convertSkills(
                listOf(
                    Skill(sportType = 1, skillLevel = 3),
                    Skill(sportType = 2, skillLevel = 2),
                    Skill(sportType = 3, skillLevel = 4)
                )
            )
        )
    )
}