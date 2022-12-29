
import crawl.parseActualChanges
import database.dbGetAllChanges
import database.dbPutChanges
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class DatabaseTest {
    @BeforeTest
    fun prepareDb(){
        initTestDb()
    }

    @Test
    fun testTransactions(){
        val actualChangesMockData = TestResources.getFile("testData/bgprevoz/output.html") ?: throw Exception("Invalid mock html")
        val changes = parseActualChanges(actualChangesMockData)

        dbPutChanges(changes)
        val c = dbGetAllChanges()
        assertEquals(c, changes)
    }
}