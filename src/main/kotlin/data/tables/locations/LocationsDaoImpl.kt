package data.tables.locations

import data.tables.DatabaseFactory.dbQuery
import data.tables.events.Events
import data.tables.events.rowToEvent
import data.model.entity.LocationInfoEntity
import data.model.entity.SportEventEntity
import data.model.request.SearchEventFilter
import data.utils.isNotNullOp
import org.jetbrains.exposed.sql.*

class LocationsDaoImpl : LocationsDao {

    override suspend fun addLocation(location: LocationInfoEntity) {
        Locations.insert {
            it[id] = location.id
            it[city] = location.city
            it[address] = location.address
            it[country] = location.country
            it[latitude] = location.latitude
            it[longitude] = location.longitude
        }
    }

    override suspend fun getLocationById(locationId: Long): LocationInfoEntity? = dbQuery {
        Locations.select {
            Locations.id eq locationId
        }.map(::rowToLocationInfo).singleOrNull()
    }

    override suspend fun getSportLocations(
        searchEventFilter: SearchEventFilter
    ): List<LocationInfoEntity> = dbQuery {

        val distanceFilter = searchEventFilter.locationFilter?.let {
            Locations.locationsFilters(it)
        } ?: Op.nullOp()

        val eventsFilter = Events.eventsFilter(searchEventFilter = searchEventFilter)
        val finalFilter = with(SqlExpressionBuilder) {
            var filter = Op.nullOp<Boolean>()
            if (eventsFilter.isNotNullOp()) {
                filter = Locations.id.inSubQuery(
                    Events.slice(Events.locationId).select {
                        Events.locationId.eq(Locations.id).and {
                            eventsFilter
                        }
                    }
                )
            }
            if (distanceFilter.isNotNullOp()) {
                filter = filter.and(distanceFilter)
            }
            filter
        }

        val query = if (finalFilter.isNotNullOp()) {
            Locations.select { finalFilter }
        } else Locations.selectAll()

        query.map(::rowToLocationInfo)
    }

    override suspend fun getLocationBookings(
        locationId: Long
    ): List<SportEventEntity> = dbQuery {
        Events.select {
            Events.id.eq(locationId)
        }.map(::rowToEvent)
    }

    override suspend fun clearAll() {
        Locations.deleteAll()
    }

    private fun rowToLocationInfo(row: ResultRow) = LocationInfoEntity(
        id = row[Locations.id],
        country = row[Locations.country],
        city = row[Locations.city],
        address = row[Locations.address],
        longitude = row[Locations.longitude],
        latitude = row[Locations.latitude]
    )
}