package data.tables.locations

import model.request.LocationFilter
import data.tables.events.Events
import data.utils.DistanceFunction
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.Table

object Locations : Table() {
    val id = varchar("id", 512)
    val country = varchar("country", 32)
    val city = varchar("city", 32)
    val address = varchar("address", 64)
    val longitude = double("longitude")
    val latitude = double("latitude")

    override val primaryKey = PrimaryKey(id)

    fun locationsFilters(locationFilter: LocationFilter): Op<Boolean> {
        return DistanceFunction(
            currentLocation = locationFilter.currentLocation
        ).lessEq(locationFilter.locationRadiusKilometers)
    }
}