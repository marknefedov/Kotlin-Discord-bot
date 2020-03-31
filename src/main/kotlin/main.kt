import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.utils.Compression
import org.discordbots.api.client.DiscordBotListAPI


lateinit var clientId : String
var recordLengthLimit = 300000L
lateinit var topggApi : DiscordBotListAPI

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

    val eventListenersToEnable = mutableListOf(
        RecordCommandHandler(),
        InstantReplayCommandHandler(),
        HelpCommand())

    if (!System.getenv("TOPGG_TOKEN").isNullOrEmpty()) {
        topggApi = DiscordBotListAPI.Builder()
            .token(System.getenv("TOPGG_TOKEN"))
            .botId(clientId)
            .build()
        eventListenersToEnable.add(StatsHandler())
    }

    JDABuilder(token).apply {
        setCompression(Compression.ZLIB)
        addEventListeners(*eventListenersToEnable.toTypedArray())
        setAutoReconnect(true)
        build()
    }
}
