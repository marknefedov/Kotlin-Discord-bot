import com.google.common.collect.EvictingQueue
import com.google.common.collect.Queues
import net.dv8tion.jda.api.audio.AudioReceiveHandler
import net.dv8tion.jda.api.audio.CombinedAudio
import java.io.ByteArrayOutputStream
import java.io.File

class AudioRecorder : AudioReceiveHandler
{
    private val buffer = ByteArrayOutputStream()
    override fun canReceiveCombined() = true

    override fun handleCombinedAudio(combinedAudio: CombinedAudio) {
        val decodedData = combinedAudio.getAudioData(1.0)
        buffer.write(decodedData)
    }

    fun endRecord(filename : String) : File
    {
        val wav = createWAVFile(buffer.toByteArray(), filename)
        buffer.reset()
        val mp3file = convertToMP3(wav)
        wav.delete()
        return mp3file
    }
}

const val bufferSize = 25000000

class InstantReplayAudioHandler : AudioReceiveHandler {
    @Suppress("UnstableApiUsage")
    private var buffer = Queues.synchronizedQueue(EvictingQueue.create<Byte>(bufferSize))
    override fun canReceiveCombined() = true

    override fun handleCombinedAudio(combinedAudio: CombinedAudio) {
        val decodedData = combinedAudio.getAudioData(1.0)
        buffer.addAll(decodedData.toTypedArray())
    }

    fun getRecordBytes(): ByteArray {
        return buffer.toByteArray()
    }
}