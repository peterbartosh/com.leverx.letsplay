package data.converter

import model.entity.SportEventEntity
import model.response.LocationInfo
import model.response.SportEvent
import model.response.User

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