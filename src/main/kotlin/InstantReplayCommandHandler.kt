import net.dv8tion.jda.api.audio.AudioReceiveHandler
import net.dv8tion.jda.api.audio.CombinedAudio
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.apache.commons.collections4.queue.CircularFifoQueue
import java.time.LocalDateTime

const val bufferSize = 50000000

class InstantReplayCommandHandler : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        super.onGuildMessageReceived(event)
        if (event.message.contentRaw.startsWith(":!record") && event.member?.voiceState?.inVoiceChannel() == true) {
            if (event.guild.audioManager.receivingHandler == null) {
                event.guild.audioManager.receivingHandler = InstantReplayAudioHandler()
            }
            event.guild.audioManager.openAudioConnection(event.member?.voiceState?.channel)
        }

        if (event.message.contentRaw.startsWith(":!replay")) {
            val replayAudioHandler = event.guild.audioManager.receivingHandler as? InstantReplayAudioHandler ?: return
            val wavRecord = createWAVFile(
                replayAudioHandler.getRecordBytes().toTypedArray().copyOf(),
                "${LocalDateTime.now()}${event.guild.id}"
            )
            val mp3Compressed = convertToMP3(wavRecord)
            wavRecord.delete()
            event.message.textChannel.sendFile(mp3Compressed).submit().thenRunAsync { mp3Compressed.delete() }
        }
    }

    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        super.onGuildVoiceLeave(event)
        if (event.member.id == clientId)
        {
            println("Bot leave from: ${event.channelLeft.name}")
            event.guild.audioManager.receivingHandler = null
        }
    }
}

class InstantReplayAudioHandler : AudioReceiveHandler {
    private var buffer = CircularFifoQueue<Byte>(bufferSize)
    override fun canReceiveCombined(): Boolean {
        return true
    }

    override fun handleCombinedAudio(combinedAudio: CombinedAudio) {
        super.handleCombinedAudio(combinedAudio)
        val decodedData = combinedAudio.getAudioData(1.0)
        buffer.addAll(decodedData.toTypedArray())
    }

    fun getRecordBytes(): Collection<Byte> {
        return buffer
    }
}