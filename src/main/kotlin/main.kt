import net.dv8tion.jda.api.JDABuilder

lateinit var clientId : String

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

    println("Starting bot")
    JDABuilder(token).apply {
        addEventListeners(
            RecordCommandHandler(),
            InstantReplayCommandHandler()
        )
        setAutoReconnect(true)
        build()
    }
}
