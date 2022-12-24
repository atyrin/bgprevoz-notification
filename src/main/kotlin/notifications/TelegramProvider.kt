package notifications

import crawl.TableRow
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
internal fun List<TableRow>.toMessages(): List<String> {
    val result = mutableListOf<String>()
    var index = 0

    forEach {
        val c = result.getOrNull(index)
        val appendableText = it.toMessage()
        if (c == null) {
            result.add(index, appendableText)
        } else {
            if (appendableText.length + c.length >= MESSAGE_SYMBOLS_LIMIT) {
                index++
                result.add(index, appendableText)
            } else result[index] += "\n\n$appendableText"
        }
    }

    return result.map { URLEncoder.encode(it, Charset.defaultCharset()) }
}

private fun TableRow.toMessage(): String {
    return """
        |Route${routeNumberCell.size.toPluralEnding()}: ${routeNumberCell.joinToString(" ")}  
        |Text: $description  
        |Dates: $dates  
    """.trimMargin()
}

private fun Int.toPluralEnding() = if (this > 1) "s" else ""