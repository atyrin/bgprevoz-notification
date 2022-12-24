import crawl.parseActualChanges
import notifications.toMessages
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class CrawlerTest {
    @Test
    fun testParseActualChanges(){
        val actualChangesMockData = TestResources.getFile("testData/bgprevoz/output.html") ?: throw Exception("Invalid mock html")
        val changes = parseActualChanges(actualChangesMockData)

        assertTrue(changes.first().routeNumberCell.contains("860J"))
        assertTrue(changes.last().linkToFullDescription == "http://www.bgprevoz.rs/linije/aktuelne-izmene/1")
    }


    @Test
    fun testCreateMessage(){
        val actualChangesMockData = TestResources.getFile("testData/bgprevoz/output.html") ?: throw Exception("Invalid mock html")
        val changes = parseActualChanges(actualChangesMockData)

        val prettyPrintedChanges = (changes+changes+changes+changes+changes).toMessages()
        assertTrue(prettyPrintedChanges.size == 3)
    }
}