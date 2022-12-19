package crawl

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val BGPREVOZ_LINK = "http://www.bgprevoz.rs/linije/aktuelne-izmene"

fun requestChanges(): String {
    val request: HttpRequest = HttpRequest.newBuilder()
        .uri(URI(BGPREVOZ_LINK))
        .GET()
        .build()

    val response = HttpClient.newHttpClient()
        .send(request, HttpResponse.BodyHandlers.ofString())

    if(response.statusCode() == 200){
        return response.body()
    }

    println(response.statusCode())
    println(response.body())
    throw Exception("Some error during request")
}