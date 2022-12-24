import crawl.BgPrevozClient
import crawl.parseActualChanges
import notifications.TelegramProvider
import notifications.toMessages


fun main() {
    val (channelName, tgBotApiToken) = loadSecrets()

    val actualChangesHtml = BgPrevozClient().actualChanges()
    val changes = parseActualChanges(actualChangesHtml)

    val prettyPrintedChanges = changes.toMessages()

    val provider = TelegramProvider(tgBotApiToken, channelName)
    prettyPrintedChanges.forEach {
        provider.sendMessage(it)
    }
}

private fun loadSecrets(): Pair<String, String> {
    val channelName = System.getenv("CHANNEL_ID") ?: throw Exception("Empty CHANNEL_ID")
    val tgBotApiToken = System.getenv("BOT_TOKEN") ?: throw Exception("Empty BOT_TOKEN")
    return Pair(channelName, tgBotApiToken)
}