package data.model.entity

data class EventUserEntity(
    val id: Long,
    val eventId: Long,
    val userId: Long,
    val isAdmin: Boolean
)