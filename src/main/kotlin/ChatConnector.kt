import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import java.io.PrintWriter
import java.net.Socket
import java.util.*


@UnstableDefault
class ChatConnector(private val scanner: Scanner, private val printWriter: PrintWriter, private val client: Socket) :
    Runnable, ChatHistoryObserver {
    // create variable for holding username
    private var name = ""

    override fun run() {
        while (true) {
            try {
                val incomingJson = scanner.nextLine()
                command(Json.parse(ChatMessage.serializer(), incomingJson))
            } catch (e: Exception) {
                ChatHistory.deregister(this)
                ChatHistory.newMessage(
                    ChatMessage(
                        Commands.Say,
                        name(),
                        "all",
                        "Has left the chat",
                        ChatTime.getChatTime()
                    ), 0
                )
                break
            }
        }
    }

    // update chat
    override fun chatUpdate(message: ChatMessage) {
        val jSonMessage = Json.stringify(ChatMessage.serializer(), message)
        println(jSonMessage)
        printWriter.println(jSonMessage)
        printWriter.flush()
    }

    // Deciding what to do
    private fun command(chatMessage: ChatMessage) {
        when (chatMessage.command()) {
            // Remove user then quit
            Commands.Quit -> {
                chatUpdate(chatMessage)
                ChatHistory.deregister(this)
                client.close()
            }
            // show chat message to everyone
            Commands.Say -> ChatHistory.newMessage(chatMessage)
            // send private message
            Commands.Whisper -> ChatHistory.privateMessage(chatMessage, this)
            // print chat console
            Commands.History -> this.chatUpdate(ChatHistory.getHistory())
            // print top chatters
            Commands.Top -> this.chatUpdate(ChatHistory.topChatters())
            Commands.Register -> {
                name = setUser(chatMessage.name())
                if (name.isNotEmpty()) {
                    ChatHistory.register(this)
                }
            }
            Commands.Users -> ChatHistory.printUsers(chatMessage)
        }
    }

    // Init username
    private fun setUser(name: String): String {
        if (Users.users.contains(name))
            printWriter.println(
                Json.stringify(
                    ChatMessage.serializer(), ChatMessage(
                        Commands.Register, "Server",
                        name, "false", ChatTime.getChatTime()
                    )
                )
            )
        else {
            Users.addUser(name)
            printWriter.println(
                Json.stringify(
                    ChatMessage.serializer(), ChatMessage(
                        Commands.Register, "Server",
                        name, "true", ChatTime.getChatTime()
                    )
                )
            )
            ChatHistory.newMessage(
                ChatMessage(
                    Commands.Say,
                    name,
                    "all",
                    "has joined the chat",
                    ChatTime.getChatTime()
                )
            )
            return name
        }
        return ""
    }

    // Name of the current user
    override fun name(): String {
        return name
    }
}