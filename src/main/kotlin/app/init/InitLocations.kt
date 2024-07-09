package app.init

import data.tables.locations.LocationsDao
import model.entity.LocationInfoEntity

suspend fun LocationsDao.addSomeData(){
    this.addLocation(
        LocationInfoEntity(
            id = 1L,
            country = "Belarus",
            city = "Minsk",
            address = "Lopatina 7",
            longitude = 27.6766,
            latitude = 53.95435
        ),
        copyId = true
    )
    this.addLocation(
        LocationInfoEntity(
            id = 2L,
            country = "Belarus",
            city = "Minsk",
            address = "Oginskogo 6",
            longitude = 27.67174,
            latitude = 53.92817
        ),
        copyId = true
    )
    this.addLocation(
        LocationInfoEntity(
            id = 3L,
            country = "Belarus",
            city = "Minsk",
            address = "Pionerskaya 3",
            longitude = 27.491230,
            latitude = 53.938370
        ),
        copyId = true
    )
}