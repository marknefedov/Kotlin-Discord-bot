import net.dv8tion.jda.api.JDABuilder

fun main(args : Array<String>) {
    val token = if (args.isEmpty())
    {
        print("Bot token:")
        (readLine() ?: return)
    }
    else
        args[0]
    JDABuilder(token).apply {
        addEventListeners(RecordCommandHandler())
        build()
    }
}
