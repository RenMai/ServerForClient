import kotlinx.serialization.Serializable

@Serializable
class ChatMessage(
    private val command: Commands,
    private val sender: String,
    private val receiver: String,
    private val message: String,
    private val timestamp: String
) {
    override fun toString(): String {
        return message
    }

    fun command(): Commands {
        return command
    }

    fun name(): String {
        return sender
    }

    fun receiver(): String {
        return receiver
    }

}