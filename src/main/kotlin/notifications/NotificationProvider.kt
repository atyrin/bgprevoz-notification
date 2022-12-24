package notifications

interface NotificationProvider {

    fun sendMessage(message: String)
}