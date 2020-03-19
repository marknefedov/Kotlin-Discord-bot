import net.dv8tion.jda.api.JDABuilder

fun main(args : Array<String>) {
    val token = when {
        args.isNotEmpty() -> args[0]
        System.getenv("BOT_TOKEN") != null -> System.getenv("BOT_TOKEN")
        else -> {
            print("Bot token:")
            (readLine() ?: return)
        }
    }
    JDABuilder(token).apply {
        addEventListeners(RecordCommandHandler())
        build()
    }
}
