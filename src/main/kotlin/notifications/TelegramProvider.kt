package notifications

import model.TimeTableChange
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.Charset

const val MESSAGE_SYMBOLS_LIMIT = 4096

class TelegramProvider(
    private val apiToken: String,
    private val chatId: String,
    private val httpClient: HttpClient = HttpClient.newHttpClient(),
) : NotificationProvider {
    override fun sendMessage(message: String) {
        val url = URI(tgUrl(message))
        val request = HttpRequest.newBuilder()
            .uri(url)
            .GET()
            .build()
        val response = httpClient
            .send(request, HttpResponse.BodyHandlers.ofString())

        println(response.statusCode())
        println(response.body())
    }

    private fun tgUrl(message: String) =
        "https://api.telegram.org/bot$apiToken/sendMessage?chat_id=$chatId&parse_mode=HTML&text=$message"
}

/**
 * Telegram message has limit in 4096 symbols. The function split output in few messages if necessary
 */
internal fun Set<TimeTableChange>.toMessages(newChanges: Set<TimeTableChange> = emptySet()): List<String> {
    val messages = mutableListOf<String>()
    var index = 0

    forEach {
        val c = messages.getOrNull(index)
        val appendableText = it.toMessage(newChanges)
        if (c == null) messages.add(index, appendableText)
        else {
            if (appendableText.length + c.length >= MESSAGE_SYMBOLS_LIMIT) {
                index++
                messages.add(index, appendableText)
            } else messages[index] += "\n\n$appendableText"
        }
    }

    return messages.map { URLEncoder.encode(it, Charset.defaultCharset()) }
}

private fun TimeTableChange.toMessage(newChanges: Set<TimeTableChange>): String {
    val isNewBadge = if (newChanges.contains(this)) " <u>(new)</u>" else ""
    return """
        |Route${routeNumberCell.size.toPluralEnding()}$isNewBadge: ${routeNumberCell.joinToString(" ")}  
        |Description: $description <a href="$linkToFullDescription">(more)</a>  
        |Dates: $dates  
    """.trimMargin()
}

private fun Int.toPluralEnding() = if (this > 1) "s" else ""