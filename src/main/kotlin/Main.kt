import crawl.BgPrevozClient
import crawl.parseActualChanges
import database.dbDeleteAllChanges
import database.dbGetAllChanges
import database.dbPutChanges
import database.initDb
import model.TimeTableChange
import model.changeTracking
import notifications.TelegramProvider
import notifications.toMessages


fun main() {
    val actualChangesHtml = BgPrevozClient().actualChanges()
    val changes = parseActualChanges(actualChangesHtml)

    val newChanges = detectNewChanges(changes)

    val provider = TelegramProvider(Secrets.tgBotToken, Secrets.tgChannelId)
    changes.toMessages(newChanges).forEach {
        provider.sendMessage(it)
    }
}

private fun detectNewChanges(changes: Set<TimeTableChange>): Set<TimeTableChange> {
    initDb(
        url = Secrets.pgUrl,
        username = Secrets.pgUsername,
        password = Secrets.pgPassword
    )

    val currentStoredData = dbGetAllChanges()
    val newChanges =
        if (currentStoredData.isEmpty()) emptySet() else changeTracking(currentStoredData, changes)

    dbDeleteAllChanges()
    dbPutChanges(changes)
    return newChanges
}


private object Secrets {

    val tgBotToken by lazy {
        load("TG_BOT_TOKEN")
    }
    val tgChannelId by lazy {
        load("TG_CHANNEL_ID")
    }
    val pgUrl by lazy {
        load("PG_URL")
    }
    val pgUsername by lazy {
        load("PG_USERNAME")
    }
    val pgPassword by lazy {
        load("PG_PASSWORD")
    }

    private fun load(id: String) =
        System.getenv(id) ?: throw Exception("Empty secret value: $id")
}