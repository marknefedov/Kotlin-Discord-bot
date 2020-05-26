import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.utils.Compression
import org.discordbots.api.client.DiscordBotListAPI


lateinit var clientId : String
var recordLengthLimit = 300000L
lateinit var topggApi : DiscordBotListAPI

fun main(args : Array<String>) {
    val token = System.getenv("BOT_TOKEN")

    clientId = System.getenv("CLIENT_ID")

    when {
        !System.getenv("RECORD_LENGTH").isNullOrEmpty() -> recordLengthLimit = System.getenv("RECORD_LENGTH").toLong()
    }

    JDABuilder.createDefault(token).apply {
        setCompression(Compression.ZLIB)
        addEventListeners(RecordCommandHandler(), InstantReplayCommandHandler(), HelpCommand())
        if (!System.getenv("TOPGG_TOKEN").isNullOrEmpty()) {
            topggApi = DiscordBotListAPI.Builder()
                .token(System.getenv("TOPGG_TOKEN"))
                .botId(clientId)
                .build()
            addEventListeners(StatsHandler())
        }
        setAutoReconnect(true)
        build()
    }
}
