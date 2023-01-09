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

    val messages = generateMessages(actualChangesHtml)
    if (messages.isEmpty()) return

    val provider = TelegramProvider(Secrets.tgBotToken, Secrets.tgChannelId)
    messages.forEach {
        provider.sendMessage(it)
    }
}

private fun generateMessages(actualChangesHtml: String): List<String> {
    val actualChanges = parseActualChanges(actualChangesHtml)

    initDb(
        url = Secrets.pgUrl,
        username = Secrets.pgUsername,
        password = Secrets.pgPassword
    )

    val storedChanges = dbGetAllChanges()
    if (actualChanges == storedChanges) return emptyList()

    val newChanges = detectNewChanges(storedChanges, actualChanges)
    updateStoredChanges(actualChanges)

    return actualChanges.toMessages(changesToMarkAsNew = newChanges)
}

private fun detectNewChanges(stored: Set<TimeTableChange>, actual: Set<TimeTableChange>) =
    if (stored.isEmpty()) emptySet() else changeTracking(previous = stored, new = actual)

private fun updateStoredChanges(actualChanges: Set<TimeTableChange>) {
    dbDeleteAllChanges()
    dbPutChanges(actualChanges)
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