import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class HelpCommand : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        super.onGuildMessageReceived(event)
        when (event.message.contentRaw) {
            "::help" -> event.message.textChannel.sendMessage("https://github.com/markusgod/Quartz-Discord-bot/blob/master/README.md")
                .queue()
        }
    }
}