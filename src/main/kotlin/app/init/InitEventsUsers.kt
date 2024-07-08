package app.init

import data.tables.events_users.EventsUsersDao

suspend fun EventsUsersDao.addSomeData(){
    this.subscribeUserToEvent(
        userId = 1,
        eventId = 2,
        isAdmin = true
    )
    this.subscribeUserToEvent(
        userId = 2,
        eventId = 2,
        isAdmin = false
    )
    this.subscribeUserToEvent(
        userId = 3,
        eventId = 2,
        isAdmin = false
    )
    this.subscribeUserToEvent(
        userId = 2,
        eventId = 1,
        isAdmin = true
    )
}