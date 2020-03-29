import net.dv8tion.jda.api.audio.AudioReceiveHandler
import ws.schild.jave.AudioAttributes
import ws.schild.jave.Encoder
import ws.schild.jave.EncodingAttributes
import ws.schild.jave.MultimediaObject
import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

const val tempFolder = "temp"

fun createWAVFile(buffer: Array<Byte>, filename: String): File {
    val directory = File(tempFolder)
    if (!directory.exists())
        directory.mkdir()

    val audioStream = AudioInputStream(
        ByteArrayInputStream(buffer.toByteArray()),
        AudioReceiveHandler.OUTPUT_FORMAT,
        buffer.size.toLong()
    )
    val wav = File("$tempFolder/$filename.wav")
    AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, wav)
    return wav
}

fun convertToMP3(file: File): File {
    val encoder = Encoder()
    val audioAttributes = AudioAttributes().apply {
        setCodec("libmp3lame")
        setBitRate(48000)
        setChannels(1)
        setSamplingRate(44100)
    }
    val attrs = EncodingAttributes().apply {
        format = "mp3"
        setAudioAttributes(audioAttributes)
    }
    val mp3 = File("$tempFolder/${file.name}.mp3")
    encoder.encode(MultimediaObject(file), mp3, attrs)
    return mp3
}