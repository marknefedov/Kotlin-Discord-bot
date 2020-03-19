import net.dv8tion.jda.api.JDABuilder

fun main(args : Array<String>) {
    if (args.isEmpty())
        print("Input bot secret as argument")

    JDABuilder(args[0]).apply {
        addEventListeners(RecordCommandHandler())
        build()
    }
}
