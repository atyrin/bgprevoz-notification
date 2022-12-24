import crawl.BgPrevozClient
import crawl.parseActualChanges
import notifications.TelegramProvider
import notifications.toMessages


fun main(args: Array<String>) {
    val (channelName, tgBotApiToken) = args.parse()

    val actualChangesHtml = BgPrevozClient().actualChanges()
    val changes = parseActualChanges(actualChangesHtml)

    val prettyPrintedChanges = changes.toMessages()

    val provider = TelegramProvider(tgBotApiToken, channelName)
    prettyPrintedChanges.forEach {
        provider.sendMessage(it)
    }
}

private fun Array<String>.parse(): Pair<String, String> {
    val channelName = getOrNull(0) ?: throw Exception("Empty channelName")
    val tgBotApiToken = getOrNull(1) ?: throw Exception("Empty tgBotApiToken")
    return Pair(channelName, tgBotApiToken)
}