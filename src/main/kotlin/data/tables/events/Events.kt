package data.tables.events

import data.model.request.LocationFilter
import data.model.request.SearchEventFilter
import data.tables.locations.Locations
import data.tables.users.Users
import data.utils.foldOp
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.Table

object Events : Table() {

    val id = long("id").autoIncrement()
    val locationId = reference(
        name = "location_id",
        refColumn = Locations.id,
        onDelete = ReferenceOption.NO_ACTION,
        onUpdate = ReferenceOption.NO_ACTION
    )
    val adminId = reference(
        name = "user_id",
        refColumn = Users.id,
        onDelete = ReferenceOption.NO_ACTION,
        onUpdate = ReferenceOption.NO_ACTION
    )
    val sportType = integer("sport_type")
    val date = long("date")
    val startTimeMinutes = integer("start_time_minutes")
    val endTimeMinutes = integer("end_time_minutes")
    val minParticipatorsAmount = integer("min_participators_amount").nullable()
    val maxParticipatorsAmount = integer("max_participators_amount").nullable()
    val skillLevel = integer("skill_level").nullable()

    override val primaryKey = PrimaryKey(id)

    fun eventsFilter(
        searchEventFilter: SearchEventFilter
    ): Op<Boolean> {
        val minParticipantsAmountFilter: Op<Boolean>? = searchEventFilter.minParticipatorsAmount?.let { min ->
            Events.minParticipatorsAmount.greater(min)
        }
        val maxParticipantsAmountFilter = searchEventFilter.maxParticipatorsAmount?.let { max ->
            Events.maxParticipatorsAmount.less(max)
        }
        val sportTypesFilter = searchEventFilter.types?.ifEmpty { null }?.let { sportTypes ->
            Events.sportType.inList(sportTypes)
        }
        val skillLevelsFilter = searchEventFilter.skillLevels?.let { skillLevels ->
            Events.skillLevel.inList(skillLevels)
        }
        val startDateFilter = searchEventFilter.startDate?.let { startDate ->
            Events.date.less(startDate)
        }
        val endDateFilter = searchEventFilter.endDate?.let { endDate ->
            Events.date.greater(endDate)
        }
        val startTimeFilter = searchEventFilter.startTime?.let { startTime ->
            Events.startTimeMinutes.less(startTime)
        }
        val endTimeFilter = searchEventFilter.endTime?.let { endTime ->
            Events.endTimeMinutes.greater(endTime)
        }
        return listOf(
            minParticipantsAmountFilter,
            maxParticipantsAmountFilter,
            sportTypesFilter,
            skillLevelsFilter,
            startDateFilter,
            endDateFilter,
            startTimeFilter,
            endTimeFilter
        ).foldOp()
    }
}