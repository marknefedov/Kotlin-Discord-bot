import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class StatsHandler : ListenerAdapter() {
    override fun onGuildReady(event: GuildReadyEvent) {
        super.onGuildReady(event)
        topggApi.setStats(event.jda.guilds.size)
    }
}