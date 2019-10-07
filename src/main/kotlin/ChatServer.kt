import kotlinx.serialization.UnstableDefault
import java.io.PrintWriter
import java.net.ServerSocket
import java.util.*
@UnstableDefault
class ChatServer () {
    private val serverSocket = ServerSocket(23) //accept connections to 30000 port

    fun serve() {
        while(true) {
            println("accepting")
            val socket = serverSocket.accept()
            println("accepted")
            // Start server
            Thread(ChatConnector(Scanner(socket.getInputStream()), PrintWriter(socket.getOutputStream(), true),socket)).start()
        }
    }
}

