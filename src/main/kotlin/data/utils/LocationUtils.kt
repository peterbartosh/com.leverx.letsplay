package data.utils

import data.model.basic.Location
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Function
import kotlin.math.cos
import kotlin.math.sqrt


class DistanceFunction(
    private val currentLocation: Location
) : Function<Float>(FloatColumnType()) {

    private val mileToKm = 1.609

    override fun toQueryBuilder(queryBuilder: QueryBuilder): Unit = queryBuilder {
        append("SQRT(POW(69.1 * (locations.latitude::float - ${currentLocation.latitude}::float), 2)")
        append("+")
        append("POW(69.1 * (${currentLocation.longitude}::float - locations.longitude::float) * COS(locations.latitude::float / 57.3), 2)")
        append(")")
        append("*$mileToKm")
    }
}