package net.spartanb312.everett.audio.decoder

import net.spartanb312.everett.utils.encryption.impl.AES
import net.spartanb312.everett.utils.misc.limitLength
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

class MP3Data(private val waveData: WaveData) : SoundData {

    override val data get() = waveData.data
    override val format get() = waveData.format
    override val sampleRate get() = waveData.sampleRate

    companion object {

        @JvmStatic
        fun create(inputStream: InputStream, name: String): MP3Data? {
            return create(AudioSystem.getAudioInputStream(BufferedInputStream(inputStream)), name)
        }

        @JvmStatic
        fun create(audioInputStream: AudioInputStream, name: String): MP3Data? {
            val relatedWavName = AES.encode(name.substringAfterLast("/"), "spartan9292")
                .replace("/", "s")
                .replace("=", "e")
                .replace("+", "p")
                .limitLength(64)
            val file = File("disk_cache/$relatedWavName")
            println(name)
            if (!file.exists()) {
                file.parentFile.mkdirs()
                file.createNewFile()
            }
            mp3ToWav(audioInputStream, file)
            val waveData = WaveData.create(file) ?: return null
            return MP3Data(waveData)
        }


        @JvmStatic
        fun mp3ToWav(audioInputStream: AudioInputStream, targetFile: File) {
            val baseFormat = audioInputStream.format
            val targetFormat = AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.sampleRate,
                16,
                baseFormat.channels,
                baseFormat.channels * 2,
                baseFormat.sampleRate,
                false
            )
            val stream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream)
            AudioSystem.write(stream, AudioFileFormat.Type.WAVE, targetFile)
        }

    }

}