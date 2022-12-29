import crawl.parseActualChanges
import model.TimeTableChange
import model.changeTracking
import notifications.toMessages
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CrawlerTest {
    @Test
    fun testParseActualChanges() {
        val actualChangesMockData =
            TestResources.getFile("testData/bgprevoz/output.html") ?: throw Exception("Invalid mock html")
        val changes = parseActualChanges(actualChangesMockData)

        assertTrue(changes.first().routeNumberCell.contains("860J"))
        assertTrue(changes.last().linkToFullDescription == "http://www.bgprevoz.rs/linije/aktuelne-izmene/1")
    }


    @Test
    fun testCreateMessage() {
        val actualChangesMockData =
            TestResources.getFile("testData/bgprevoz/output.html") ?: throw Exception("Invalid mock html")
        val changes = parseActualChanges(actualChangesMockData)

        val prettyPrintedChanges = (changes + changes + changes + changes + changes).toMessages()
        assertTrue(prettyPrintedChanges.size == 3)
    }


    @Test
    fun testChangeTracking() {
        val before = setOf(
            TimeTableChange(
                routeNumberCell = setOf("1", "2"),
                changeType = "change",
                description = "description",
                dates = "01-02",
                linkToFullDescription = null
            ),
            TimeTableChange(
                routeNumberCell = setOf("3", "4"),
                changeType = "change",
                description = "description 2",
                dates = "01-02",
                linkToFullDescription = null
            )
        )

        val after = setOf(
            TimeTableChange(
                routeNumberCell = setOf("1", "2"),
                changeType = "change",
                description = "description",
                dates = "01-02",
                linkToFullDescription = null
            ),
            TimeTableChange(
                routeNumberCell = setOf("5", "6"),
                changeType = "change",
                description = "description 3",
                dates = "01-02",
                linkToFullDescription = null
            )
        )

        val new = changeTracking(before, after)
        assertEquals(
            setOf(
                TimeTableChange(
                    routeNumberCell = setOf("5", "6"),
                    changeType = "change",
                    description = "description 3",
                    dates = "01-02",
                    linkToFullDescription = null
                )
            ), new
        )
    }
}