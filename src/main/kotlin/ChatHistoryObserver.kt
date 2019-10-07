interface ChatHistoryObserver {
    fun chatUpdate(message: ChatMessage)
    fun name(): String
}