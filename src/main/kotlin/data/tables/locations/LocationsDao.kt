package data.tables.locations

import data.model.entity.LocationInfoEntity
import data.model.entity.SportEventEntity
import data.model.request.SearchEventFilter

interface LocationsDao {

    suspend fun addLocation(location: LocationInfoEntity)

    suspend fun getLocationById(locationId: Long): LocationInfoEntity?

    suspend fun getSportLocations(
        searchEventFilter: SearchEventFilter
    ): List<LocationInfoEntity>

    suspend fun getLocationBookings(
        locationId: Long
    ): List<SportEventEntity>

    suspend fun clearAll()
}