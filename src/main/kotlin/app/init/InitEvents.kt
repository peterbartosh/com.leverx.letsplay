package app.init

import data.tables.events.EventsDao
import model.entity.SportEventEntity

private const val MillisInDay = 1000 * 60 * 60 * 24L

suspend fun EventsDao.addSomeData(){
    this.createEvent(
        sportEventEntity = SportEventEntity(
            id = 1L,
            adminId = 2,
            locationId = 1,
            sportType = 1,
            date = System.currentTimeMillis() + 2 * MillisInDay,
            startTimeMinutes = 1200,
            endTimeMinutes = 1260,
            minParticipatorsAmount = 3,
            maxParticipatorsAmount = 10,
            skillLevel = 1
        ),
        copyId = true
    )
    this.createEvent(
        sportEventEntity = SportEventEntity(
            id = 2L,
            adminId = 2,
            locationId = 2,
            sportType = 3,
            date = System.currentTimeMillis() + 3 * MillisInDay,
            startTimeMinutes = 720,
            endTimeMinutes = 840,
            minParticipatorsAmount = 1,
            maxParticipatorsAmount = 3,
            skillLevel = 4
        ),
        copyId = true
    )
    this.createEvent(
        sportEventEntity = SportEventEntity(
            id = 3L,
            adminId = 3,
            locationId = 1,
            sportType = 2,
            date = System.currentTimeMillis(),
            startTimeMinutes = 600,
            endTimeMinutes = 720,
            minParticipatorsAmount = 2,
            maxParticipatorsAmount = 5,
            skillLevel = 1
        ),
        copyId = true
    )
}