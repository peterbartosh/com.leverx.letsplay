package data.tables.events

import model.request.SearchEventFilter
import data.tables.locations.Locations
import data.tables.users.Users
import data.utils.foldAnd
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.Table

val eventsAutoIncSeqName = "event_id_seq"

object Events : Table() {

    val id = long("id").autoIncrement(eventsAutoIncSeqName)
    val locationId = reference(
        name = "location_id",
        refColumn = Locations.id,
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
            minParticipatorsAmount.greaterEq(min)
        }
        val maxParticipantsAmountFilter = searchEventFilter.maxParticipatorsAmount?.let { max ->
            maxParticipatorsAmount.lessEq(max)
        }
        val sportTypesFilter = searchEventFilter.types?.ifEmpty { null }?.let { sportTypes ->
            sportType.inList(sportTypes)
        }
        val skillLevelsFilter = searchEventFilter.skillLevels?.ifEmpty { null }?.let { skillLevels ->
            skillLevel.inList(skillLevels)
        }
        val startDateFilter = searchEventFilter.startDate?.let { startDate ->
            date.lessEq(startDate)
        }
        val endDateFilter = searchEventFilter.endDate?.let { endDate ->
            date.greaterEq(endDate)
        }
        val startTimeFilter = searchEventFilter.startTime?.let { startTime ->
            startTimeMinutes.lessEq(startTime)
        }
        val endTimeFilter = searchEventFilter.endTime?.let { endTime ->
            endTimeMinutes.greaterEq(endTime)
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
        ).foldAnd()
    }
}