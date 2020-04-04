import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.exceptions.PermissionException
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.time.LocalDateTime

class InstantReplayCommandHandler : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        super.onGuildMessageReceived(event)
        val audioManager = event.guild.audioManager
        when {
            event.message.contentRaw == "::irecord" && event.member?.voiceState?.inVoiceChannel() == true -> {
                if (audioManager.receivingHandler == null) {
                    try {
                        audioManager.receivingHandler = InstantReplayAudioHandler()
                    }catch (e : OutOfMemoryError){
                        println(e.toString() + e.message)
                        event.message.textChannel.sendMessage("Server is out of memory, try again later").queue()
                        event.guild.audioManager.receivingHandler = null
                        System.gc()
                        return
                    }
                }
                val recordChannel = event.member?.voiceState?.channel
                try {
                    audioManager.openAudioConnection(recordChannel)
                }catch (e : PermissionException)
                {
                    println(e.toString() + e.message)
                    e.message?.let { event.message.textChannel.sendMessage(it).queue() }
                    audioManager.receivingHandler = null
                    return
                }
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
                try {
                event.message.textChannel.sendFile(mp3Compressed).submit().thenRunAsync { mp3Compressed.delete() }
                }catch (e : PermissionException) {
                    println(e.toString() + e.message)
                    e.message?.let { event.message.textChannel.sendMessage(it).queue() }
                    event.guild.audioManager.receivingHandler = null
                    return
                }
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