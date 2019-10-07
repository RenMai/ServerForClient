import kotlinx.serialization.UnstableDefault
@UnstableDefault
object ChatHistory : ChatHistoryObservable{

    private val observers: MutableSet<ChatHistoryObserver> = mutableSetOf() // list of registered observers
    private val chatHistory: MutableList<ChatMessage> = mutableListOf() // list of messages
    private val topChatter = mutableMapOf<String, Int>() // each users message count

    // register observer
    override fun register (observer: ChatHistoryObserver) {
        observers.add(observer)
        println("Welcome ${observer.name()} to the Chat Server")
    }

    // deregister observer and remove from userlist
    override fun deregister(observer: ChatHistoryObserver) {
        topChatter.remove(observer.name())
        observers.remove(observer)
        Users.removeUser(observer.name())
        newMessage(ChatMessage(Commands.Say,observer.name(), "all","Has left the chat",ChatTime.getChatTime()), 0)
    }
    // show the message to everyone
    override fun newMessage(message: ChatMessage, deregister: Int  ) {
        chatHistory.add(message)
        for (n in observers) {
            if (message.name() != n.name()) {
                n.chatUpdate(message)
            } else {
                n.chatUpdate(ChatMessage(message.command(),"Me", message.receiver(), message.toString(),ChatTime.getChatTime()))
            }

        }
        if (deregister == 1 ) {
            addToCount(message) // count top chatter
        }
    }
    // sends a private message and adds 1 to count for the user

    override fun privateMessage(message: ChatMessage, sender: ChatConnector) {
        for (n in observers) {
            if (n.name() == message.receiver()) {
                println("sending whisper to: $n ... ${n.name()}:")
                n.chatUpdate(message)
                sender.chatUpdate(ChatMessage(message.command(),"Me->${message.receiver()}", message.receiver(), message.toString(),ChatTime.getChatTime()))
                addToCount(message)
            }
        }
    }
    // get chat history as a string
    fun getHistory (): ChatMessage{
        var history = "\n"
        for (n in chatHistory) {
            history += "${n.name()}-> ${n.receiver()}: $n\n"
        }
        return  ChatMessage(Commands.History, "server", "", history,ChatTime.getChatTime())
    }

    // print the top chatters
    fun topChatters(): ChatMessage {
        var i = 1
        var message = ""
        // sorts the chat map -> makes it into a list and sorts it in reverse order based on value -> makes it back into the map
        val sorted = topChatter.toList().sortedBy { (_, value) -> value}.reversed().toMap()
        for (n in sorted) {
            message += "\n$i: ${n.key} ${n.value} messages."
            i++
            if (i == 11) break
        }
        return ChatMessage(Commands.Top,"server","",message,ChatTime.getChatTime())
    }
    // count top chatter
    private fun addToCount (chatMessage: ChatMessage) {
        val value = topChatter[chatMessage.name()]
        if (value != null) {
            topChatter[chatMessage.name()] = value + 1
        } else  topChatter[chatMessage.name()] = 0
    }

    fun printUsers(chatMessage: ChatMessage) {
        for (n in observers){
            if (n.name() == chatMessage.name()){
                val message= ChatMessage(
                    Commands.Users,
                    "server",
                    chatMessage.name(),
                    Users.users.toString(),
                    ChatTime.getChatTime()
                )
                n.chatUpdate(message)
            }
        }
    }
}