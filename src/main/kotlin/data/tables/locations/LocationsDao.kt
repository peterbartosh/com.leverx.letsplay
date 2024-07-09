package data.tables.locations

import model.entity.LocationInfoEntity
import model.entity.SportEventEntity
import model.request.SearchEventFilter

interface LocationsDao {

    suspend fun addLocation(location: LocationInfoEntity, copyId: Boolean = false)

    suspend fun getLocationById(locationId: Long): LocationInfoEntity?

    suspend fun getSportLocations(
        searchEventFilter: SearchEventFilter
    ): List<LocationInfoEntity>

    suspend fun getLocationBookings(
        locationId: Long
    ): List<SportEventEntity>

    suspend fun clearAll()
}