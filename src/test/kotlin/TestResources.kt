import database.initSchema
import org.jetbrains.exposed.sql.Database

class TestResources {
    companion object {
        fun getFile(path: String): String? = this::class.java.getResource(path)?.readText()
    }
}

fun initTestDb() {
    Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

    initSchema()
}