@file:JvmName("InitEventsKt")

package app.init

import data.service.ClientService
import data.tables.DatabaseFactory.dbQuery
import model.basic.Location
import model.request.LocationInfoBody
import model.request.SportEventBody

private const val MillisInDay = 1000 * 60 * 60 * 24L


suspend fun ClientService.addSomeData() = dbQuery {

    this@addSomeData.createEvent(
        sportEventBody = SportEventBody(
            adminId = 1,
            sportType = 1,
            date = System.currentTimeMillis() + 2 * MillisInDay,
            startTimeMinutes = 1200,
            endTimeMinutes = 1260,
            minParticipatorsAmount = 3,
            maxParticipatorsAmount = 10,
            skillLevel = 1,
            locationInfoBody = LocationInfoBody(
                id = "0",
                country = "Belarus",
                city = "Minsk",
                address = "Lopatina 7",
                location = Location(
                    longitude = 27.6766,
                    latitude = 53.95435
                ),
            )
        )
    )
    this@addSomeData.createEvent(
        sportEventBody = SportEventBody(
            adminId = 1,
            sportType = 2,
            date = System.currentTimeMillis() + 1 * MillisInDay,
            startTimeMinutes = 1660,
            endTimeMinutes = 1780,
            minParticipatorsAmount = 2,
            maxParticipatorsAmount = 2,
            skillLevel = 3,
            locationInfoBody = LocationInfoBody(
                id = "1",
                country = "Belarus",
                city = "Minsk",
                address = "Oginskogo 6",
                location = Location(
                    longitude = 27.67174,
                    latitude = 53.92817
                )
            )
        )
    )
    this@addSomeData.createEvent(
        sportEventBody = SportEventBody(
            adminId = 2,
            sportType = 3,
            date = System.currentTimeMillis() + 4 * MillisInDay,
            startTimeMinutes = 1260,
            endTimeMinutes = 1320,
            minParticipatorsAmount = 2,
            maxParticipatorsAmount = 4,
            skillLevel = 4,
            locationInfoBody = LocationInfoBody(
                id = "2",
                country = "Belarus",
                city = "Minsk",
                address = "Pionerskaya 3",
                location = Location(
                    longitude = 27.491230,
                    latitude = 53.938370
                )
            )
        )
    )

    this@addSomeData.subscribeUserToEvent(
        2, 1, false
    )
    this@addSomeData.subscribeUserToEvent(
        2, 2, false
    )
    this@addSomeData.subscribeUserToEvent(
        1, 2, false
    )
}