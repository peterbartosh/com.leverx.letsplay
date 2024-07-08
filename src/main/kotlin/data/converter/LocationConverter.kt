package data.converter

import data.model.basic.Location
import data.model.entity.LocationInfoEntity
import data.model.response.LocationInfo

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