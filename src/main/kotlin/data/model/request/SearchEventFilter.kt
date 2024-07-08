package data.model.request

data class SearchEventFilter(
    val types: List<Int>? = null,
    val skillLevels: List<Int>? = null,
    val minParticipatorsAmount: Int? = null,
    val maxParticipatorsAmount: Int? = null,
    val locationFilter: LocationFilter? = null,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val startTime: Int? = null,
    val endTime: Int? = null,
    val skip: Int? = null,
    val limit: Int? = null
)