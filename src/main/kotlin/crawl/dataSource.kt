package crawl

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val BGPREVOZ_LINK = "http://www.bgprevoz.rs"
const val ACTUAL_CHANGES_LINK = "/linije/aktuelne-izmene"

class BgPrevozClient(private val httpClient: HttpClient = HttpClient.newHttpClient()) {

    fun actualChanges(): String {
        val request: HttpRequest = getRequest(URI(BGPREVOZ_LINK + ACTUAL_CHANGES_LINK))
        return httpRequest(request)
    }

    private fun httpRequest(request: HttpRequest): String {
        val response = httpClient
            .send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() == 200) {
            return response.body()
        }

        println(response.statusCode())
        println(response.body())
        throw Exception("Some error during request")
    }
}

private fun getRequest(url: URI) = HttpRequest.newBuilder()
    .uri(url)
    .GET()
    .build()