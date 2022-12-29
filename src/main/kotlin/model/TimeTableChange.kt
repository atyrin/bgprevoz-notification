package model

data class TimeTableChange(
    val routeNumberCell: Set<String>,
    val changeType: String, val description: String, val dates: String, val linkToFullDescription: String?,
)

fun changeTracking(
    previous: Set<TimeTableChange>,
    new: Set<TimeTableChange>,
): Set<TimeTableChange> {
    if (previous == new) return emptySet()
    return new.minus(previous)
}