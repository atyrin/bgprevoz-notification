import crawl.BgPrevozClient
import crawl.parseActualChanges
import notifications.TelegramProvider
import notifications.toMessages


fun main(args: Array<String>) {
    val channelName = args.getOrNull(0) ?: throw Exception()
    val tgBotApiToken = args.getOrNull(1) ?: throw Exception()

    val actualChangesHtml = BgPrevozClient().actualChanges()
    val changes = parseActualChanges(actualChangesHtml)

    val prettyPrintedChanges = changes.toMessages()

    val provider = TelegramProvider(tgBotApiToken, channelName)
    prettyPrintedChanges.forEachIndexed { ind, it ->
        provider.sendMessage(it)
        println("Message $ind: $it")
    }
}

