package data.tables.locations

import model.entity.LocationInfoEntity
import model.entity.SportEventEntity
import model.request.SearchEventFilter

interface LocationsDao {

    suspend fun addLocation(location: LocationInfoEntity, copyId: Boolean = false): Result<LocationInfoEntity?>

    suspend fun getLocationById(locationId: Long): Result<LocationInfoEntity?>

    suspend fun getSportLocations(searchEventFilter: SearchEventFilter): Result<List<LocationInfoEntity>>

    suspend fun getLocationEvents(locationId: Long): Result<List<SportEventEntity>>

    suspend fun clearAll(): Result<Int>
}