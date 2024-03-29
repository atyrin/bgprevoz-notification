package database

import model.TimeTableChange
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun initDb(url: String, username: String, password: String) {
    Database.connect(
        url = url,
        driver = "org.postgresql.Driver",
        user = username, password = password
    )

    initSchema()
}

internal fun initSchema() {
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(TimeTableChanges)
    }
}

fun dbPutChanges(changes: Collection<TimeTableChange>) {
    transaction {
        changes.forEach { change ->
            TimeTableChanges.insert {
                it[routes] = change.routeNumberCell.packRoutes()
                it[changeType] = change.changeType
                it[description] = change.description
                it[dates] = change.dates
                it[fullDescriptionLink] = change.linkToFullDescription
            }
        }
    }
}

fun dbGetAllChanges(): Set<TimeTableChange> = transaction {
    TimeTableChanges.selectAll().map { it.toTimeTableChange() }.toSet()
}

private fun ResultRow.toTimeTableChange() = TimeTableChange(
    routeNumberCell = this[TimeTableChanges.routes].unpackRoutes(),
    changeType = this[TimeTableChanges.changeType],
    description = this[TimeTableChanges.description],
    dates = this[TimeTableChanges.dates],
    linkToFullDescription = this[TimeTableChanges.fullDescriptionLink]
)

private fun Set<String>.packRoutes() = joinToString(",")
private fun String.unpackRoutes() = split(",").toSet()

fun dbDeleteAllChanges() = transaction {
    TimeTableChanges.deleteAll()
}

object TimeTableChanges : IntIdTable() {
    val routes: Column<String> = text("routes")
    val changeType: Column<String> = varchar("change_type", 250)
    val description: Column<String> = text("description")
    val dates: Column<String> = varchar("dates", 150)
    val fullDescriptionLink: Column<String?> = text("full_description_link").nullable()
}