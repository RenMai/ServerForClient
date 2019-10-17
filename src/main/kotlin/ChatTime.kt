import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

//to help print the current time
object ChatTime {
    private val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
    fun getChatTime(): String {
        return LocalDateTime.now().format(formatter)
    }
}