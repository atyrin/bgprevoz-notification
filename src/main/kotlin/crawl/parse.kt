package crawl

private val tableRow = "<tr class=\"aktuelne_izmene\">(.*?)</tr>"
    .toRegex(RegexOption.DOT_MATCHES_ALL)

private val tableCellExtrasRegex = "<td class=.*?>(.*?)</td>"
    .toRegex(RegexOption.DOT_MATCHES_ALL)

private val tableCellRouteNumber = "<th class=.*>(.)*?</th>"
    .toRegex(RegexOption.DOT_MATCHES_ALL)

private val routeNumberInCell = "<a .*?href=.*?>(.*?)</a>"
    .toRegex(RegexOption.DOT_MATCHES_ALL)

private val linkToFullDescriptionRegex = "<a href=\"(.*?)\".*>.*?</a>"
    .toRegex(RegexOption.DOT_MATCHES_ALL)


fun parseActualChanges(actualChanges: String): List<TableRow> {
    return parseTable(actualChanges)
}

private fun parseTable(input: String) =
    tableRow.findAll(input).map { it.groupValues[1] }
        .mapNotNull { it.toTableRow() }
        .toList()

private fun String.toTableRow(): TableRow? {
    val routeNumberCell = tableCellRouteNumber.find(this)?.value ?: return null
    val otherCells = tableCellExtrasRegex.findAll(this).map { it.groupValues[1] }.toList()
    val linkToFullDescription = linkToFullDescriptionRegex.find(otherCells[3])?.groupValues?.get(1)

    return TableRow(
        routeNumberCell = routeNumberCell.toRouteNumberCell(),
        description = otherCells[0].trim(),
        changeType = otherCells[1].trim(),
        dates = otherCells[2].trim(),
        linkToFullDescription = linkToFullDescription
    )
}

internal fun String.toRouteNumberCell() = routeNumberInCell.findAll(this).map { it.groupValues[1] }.toList()

data class TableRow(
    val routeNumberCell: List<String>,
    val changeType: String, val description: String, val dates: String, val linkToFullDescription: String?,
)
