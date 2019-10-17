import kotlinx.serialization.UnstableDefault

@UnstableDefault
interface ChatHistoryObservable {
    fun register(observer: ChatHistoryObserver)
    fun deregister(observer: ChatHistoryObserver)
    fun newMessage(message: ChatMessage, deregister: Int = 1)
    fun privateMessage(message: ChatMessage, sender: ChatConnector)
}