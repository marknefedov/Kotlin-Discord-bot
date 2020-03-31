import com.google.common.collect.EvictingQueue
import net.dv8tion.jda.api.audio.AudioReceiveHandler
import net.dv8tion.jda.api.audio.CombinedAudio
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.time.LocalDateTime

const val bufferSize = 25000000

class InstantReplayCommandHandler : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        super.onGuildMessageReceived(event)
        val audioManager = event.guild.audioManager
        when {
            event.message.contentRaw == "::irecord" && event.member?.voiceState?.inVoiceChannel() == true -> {
                if (audioManager.receivingHandler == null) {
                    audioManager.receivingHandler = InstantReplayAudioHandler()
                }
                val recordChannel = event.member?.voiceState?.channel
                audioManager.openAudioConnection(recordChannel)
                println("Start replay recording on ${event.guild.name} | ${recordChannel?.name}")
            }
            event.message.contentRaw =="::ireplay" -> {
                val replayAudioHandler = audioManager.receivingHandler as? InstantReplayAudioHandler ?: return
                val wavRecord = createWAVFile(
                    replayAudioHandler.getRecordBytes(),
                    "${LocalDateTime.now()}${event.guild.id}"
                )
                val mp3Compressed = convertToMP3(wavRecord)
                wavRecord.delete()
                event.message.textChannel.sendFile(mp3Compressed).submit().thenRunAsync { mp3Compressed.delete() }
            }
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
    @Suppress("UnstableApiUsage")
    private var buffer = EvictingQueue.create<Byte>(bufferSize)
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