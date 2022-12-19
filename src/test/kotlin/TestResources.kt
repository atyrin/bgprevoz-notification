class TestResources {
    companion object {
        fun getFile(path: String): String? = this::class.java.getResource(path)?.readText()
    }
}