package data.tables.locations

import model.entity.LocationInfoEntity
import model.entity.SportEventEntity
import model.request.SearchEventFilter

interface LocationsDao {

    suspend fun addLocation(location: LocationInfoEntity): Result<LocationInfoEntity?>

    suspend fun getLocationById(locationId: String): Result<LocationInfoEntity?>

    suspend fun getSportLocations(searchEventFilter: SearchEventFilter): Result<List<LocationInfoEntity>>

    suspend fun getLocationEvents(locationId: Long): Result<List<SportEventEntity>>

    suspend fun clearAll(): Result<Int>
}