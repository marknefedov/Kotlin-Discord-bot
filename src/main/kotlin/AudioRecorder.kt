import net.dv8tion.jda.api.audio.AudioReceiveHandler
import net.dv8tion.jda.api.audio.CombinedAudio
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.io.File
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit
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
        val textChannel = message.textChannel
        when {
            !isRecording.getOrPut(guildIdLong){false} && message.contentRaw.startsWith("::record") -> {
                if ((guild.getMemberById(guildMemberId)?.voiceState ?: return).inVoiceChannel())
                {
                    var scheduleTime = 300000L
                    stopTask[guildIdLong]?.cancel()
                    isRecording[guildIdLong] = true
                    guild.audioManager.receivingHandler = recorders.getOrPut(guildIdLong){AudioRecorder()}
                    guild.audioManager.openAudioConnection((guild.getMemberById(guildMemberId)?.voiceState ?: return).channel)
                    val splitMessage = message.contentRaw.split(" ")
                    if (splitMessage.size > 1 && splitMessage[1].toLongOrNull() != null) {
                        scheduleTime = splitMessage[1].toLong() * 1000 // to seconds
                        scheduleStop(textChannel, scheduleTime)
                    }else scheduleStop(textChannel, scheduleTime)
                    val hms = String.format(
                        "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(scheduleTime),
                        TimeUnit.MILLISECONDS.toMinutes(scheduleTime) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(scheduleTime) % TimeUnit.MINUTES.toSeconds(1)
                    )
                    event.channel.sendMessage("Starting a $hms long recording. Use `::stop` to stop record earlier.").queue()
                }
            }
            isRecording.getOrPut(guildIdLong){false} && message.contentRaw =="::stop" -> {
                if ((guild.getMemberById(guildMemberId)?.voiceState ?: return).inVoiceChannel())
                    stopRecord(textChannel)
            }
        }
    }

    private fun stopRecord(textChannel: TextChannel)
    {
        val guildIdLong = textChannel.guild.idLong
        isRecording[guildIdLong] = false
        textChannel.guild.audioManager.closeAudioConnection()
        val filename = LocalDateTime.now().toString() + textChannel.guild.id
        val record = (recorders[guildIdLong] ?: return).endRecord(filename)
        textChannel.sendFile(record).queue()
        record.delete()
        stopTask[guildIdLong]?.cancel()
    }

    private fun scheduleStop(textChannel: TextChannel, time : Long)
    {
        stopTask[textChannel.guild.idLong] = Timer("Limit records", false).schedule(time) {
            stopRecord(textChannel)
        }
    }
}

class AudioRecorder : AudioReceiveHandler
{
    private var buffer = mutableListOf<Byte>()
    override fun canReceiveCombined(): Boolean {
        return true
    }

    override fun handleCombinedAudio(combinedAudio: CombinedAudio) {
        super.handleCombinedAudio(combinedAudio)
        val decodedData = combinedAudio.getAudioData(1.0)
        buffer.addAll(decodedData.toList())
    }

    fun endRecord(filename : String) : File
    {
        val wav = createWAVFile(buffer.toTypedArray(), filename)
        val mp3file = convertToMP3(wav)
        wav.delete()
        return mp3file
    }
}