package data.converter

import data.model.entity.SportEventEntity
import data.model.response.LocationInfo
import data.model.response.SportEvent
import data.model.response.User

fun SportEventEntity.toDto(
    admin: User,
    locationData: LocationInfo,
    participators: List<User>
) = SportEvent(
    id = id,
    sportType = sportType,
    admin = admin,
    locationData = locationData,
    participators = participators,
    date = date,
    startTimeMinutes = startTimeMinutes,
    endTimeMinutes = endTimeMinutes,
    minParticipatorsAmount = minParticipatorsAmount,
    maxParticipatorsAmount = maxParticipatorsAmount,
    skillLevel = skillLevel
)