import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.schedule

class RecordCommandHandler : ListenerAdapter()
{
    private var isRecording = mutableMapOf<Long, Boolean>()
    private var recorders = mutableMapOf<Long, AudioRecorder>()
    private var stopTask = mutableMapOf<Long, TimerTask?>()

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        super.onGuildMessageReceived(event)
        val guild = event.guild
        val guildIdLong = guild.idLong
        val guildMemberId = event.author.id
        val message = event.message
        var textChannel : MessageChannel = message.textChannel
        when {
            !isRecording.getOrPut(guildIdLong){false} && message.contentRaw.startsWith("::record") -> {
                if ((guild.getMemberById(guildMemberId)?.voiceState ?: return).inVoiceChannel())
                {
                    var scheduleTime = 300000L
                    stopTask[guildIdLong]?.cancel()
                    isRecording[guildIdLong] = true
                    guild.audioManager.receivingHandler = recorders.getOrPut(guildIdLong){AudioRecorder()}
                    val recordChannel = (guild.getMemberById(guildMemberId)?.voiceState ?: return).channel
                    guild.audioManager.openAudioConnection(recordChannel)
                    val splitMessage = message.contentRaw.split(" ")
                    if (splitMessage.size > 1 && splitMessage[1].toLongOrNull() != null) {
                        val newScheduleTime = splitMessage[1].toLong() * 1000
                        if (newScheduleTime < recordLengthLimit)
                            scheduleTime = newScheduleTime
                        else
                            event.channel.sendMessage("Requested record is longer than current limit (${toHumanTime(recordLengthLimit)}).").queue()
                    }
                    if (event.message.mentionedMembers.isNotEmpty())
                    {
                        val user = event.message.mentionedMembers.first().user
                        user.openPrivateChannel().queue { textChannel = it }
                    }
                    scheduleStop(textChannel, guild, scheduleTime)
                    val hms = toHumanTime(scheduleTime)
                    event.channel.sendMessage("Starting a $hms long recording. Use `::stop` to stop record earlier.").queue()
                    println("Start recording on ${guild.name} | ${recordChannel?.name}")
                }
            }
            isRecording.getOrPut(guildIdLong){false} && message.contentRaw =="::stop" -> {
                if ((guild.getMemberById(guildMemberId)?.voiceState ?: return).inVoiceChannel())
                    stopTask[guild.idLong]?.run()
            }
        }
    }

    private fun stopRecord(messageChannel: MessageChannel, guild : Guild)
    {
        isRecording[guild.idLong] = false
        guild.audioManager.closeAudioConnection()
        val filename = LocalDateTime.now().toString() + guild.id
        val record = (recorders[guild.idLong] ?: return).endRecord(filename)
        messageChannel.sendFile(record).submit().thenRunAsync { record.delete() }
        stopTask[guild.idLong]?.cancel()
    }

    private fun scheduleStop(messageChannel: MessageChannel, guild: Guild, time : Long)
    {
        stopTask[guild.idLong] = Timer("Limit records", false).schedule(time) {
            stopRecord(messageChannel, guild)
        }
    }
}