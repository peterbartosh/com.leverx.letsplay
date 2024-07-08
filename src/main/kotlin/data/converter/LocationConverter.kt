package data.converter

import model.basic.Location
import model.entity.LocationInfoEntity
import model.response.LocationInfo

fun LocationInfoEntity.toDto() = LocationInfo(
    id = id,
    country = country,
    city = city,
    address = address,
    location = Location(
        longitude = longitude,
        latitude = latitude
    )
)