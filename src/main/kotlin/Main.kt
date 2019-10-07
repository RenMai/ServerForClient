import kotlinx.serialization.UnstableDefault

@UnstableDefault
fun main()
{
    val server = ChatServer()
    server.serve()
}