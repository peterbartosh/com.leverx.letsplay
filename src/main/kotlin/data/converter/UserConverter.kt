package data.converter

import model.basic.Skill
import model.entity.UserEntity
import model.response.User

// skills - "02,25" == Skill(Basketball, 2), Skill(Football, 5)
fun convertSkills(skills: String?) = skills?.split("," )?.mapNotNull {
    try {
        val sportType = it.first().toString().toInt()
        val skillLevel = it.last().toString().toInt()
        Skill(sportType = sportType, skillLevel = skillLevel)
    } catch (e: NoSuchElementException) {
        null
    }
}

fun convertSkills(skills: List<Skill>) = skills.joinToString(",") {
    it.sportType.toString() + it.skillLevel.toString()
}

fun UserEntity.toDto() = User(
    id = id,
    name = username,
    age = age,
    rating = rating,
    skills = convertSkills(skills)
)