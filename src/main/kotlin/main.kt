import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.utils.Compression
import java.util.concurrent.TimeUnit

lateinit var clientId : String
var recordLengthLimit = 300000L

fun main(args : Array<String>) {
    val token = when {
        args.isNotEmpty() -> args[0]
        System.getenv("BOT_TOKEN") != null -> System.getenv("BOT_TOKEN")
        else -> {
            println("Bot token:")
            (readLine() ?: return)
        }
    }

    clientId = when {
        args.size > 1 -> args[1]
        System.getenv("CLIENT_ID") != null -> System.getenv("CLIENT_ID")
        else -> {
            println("Client id: ")
            (readLine() ?: return)
        }
    }

    when {
        !System.getenv("RECORD_LENGTH").isNullOrEmpty() -> recordLengthLimit = System.getenv("RECORD_LENGTH").toLong()
    }

    JDABuilder(token).apply {
        setCompression(Compression.ZLIB)
        addEventListeners(
            RecordCommandHandler(),
            InstantReplayCommandHandler(),
            HelpCommand()
        )
        setAutoReconnect(true)
        build()
    }
}

fun toHumanTime(time : Long) : String {
    return String.format(
        "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time),
        TimeUnit.MILLISECONDS.toMinutes(time) % TimeUnit.HOURS.toMinutes(1),
        TimeUnit.MILLISECONDS.toSeconds(time) % TimeUnit.MINUTES.toSeconds(1)
    )
}