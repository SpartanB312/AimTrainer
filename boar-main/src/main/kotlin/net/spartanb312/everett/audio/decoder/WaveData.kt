package net.spartanb312.everett.audio.decoder

import net.spartanb312.everett.utils.Logger
import net.spartanb312.everett.utils.ResourceHelper
import org.lwjgl.openal.AL10
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

class WaveData(
    override val data: ByteBuffer,
    override val format: Int,
    override val sampleRate: Int
) : SoundData {

    companion object {

        @JvmStatic
        fun create(file: File): WaveData? {
            return create(file.inputStream())
        }

        @JvmStatic
        fun create(path: String): WaveData? {
            return ResourceHelper.getResourceStream(path)?.let { create(it) }
        }

        @JvmStatic
        fun create(input: InputStream): WaveData? {
            return try {
                create(AudioSystem.getAudioInputStream(BufferedInputStream(input)))
            } catch (e: java.lang.Exception) {
                Logger.error("Unable to create from input stream, " + e.message)
                null
            }
        }

        @JvmStatic
        fun create(buffer: ByteArray): WaveData? {
            return try {
                create(AudioSystem.getAudioInputStream(BufferedInputStream(ByteArrayInputStream(buffer))))
            } catch (e: java.lang.Exception) {
                Logger.error("Unable to create from byte array, " + e.message)
                null
            }
        }

        @JvmStatic
        fun create(input: AudioInputStream): WaveData? {
            // Configure format
            val audioFormat = input.format
            val channels = audioFormat.channels
            val sampleSize = audioFormat.sampleSizeInBits
            val channelFlag = when {
                channels == 1 && sampleSize == 8 -> AL10.AL_FORMAT_MONO8
                channels == 1 && sampleSize == 16 -> AL10.AL_FORMAT_MONO16
                channels == 2 && sampleSize == 8 -> AL10.AL_FORMAT_STEREO8
                channels == 2 && sampleSize == 16 -> AL10.AL_FORMAT_STEREO16
                else -> {
                    Logger.error("Only mono or stereo is supported")
                    return null
                }
            }

            // Read data
            var available = input.available()
            if (available <= 0) available = channels * input.frameLength.toInt() * sampleSize / 8
            val buf = ByteArray(available)
            var read: Int
            var total = 0
            while (input.read(buf, total, buf.size - total).also { read = it } != -1 && total < buf.size) {
                total += read
            }
            input.close()
            return WaveData(
                convertAudioBytes(
                    buf,
                    sampleSize == 16,
                    if (audioFormat.isBigEndian) ByteOrder.BIG_ENDIAN else ByteOrder.LITTLE_ENDIAN
                ),
                channelFlag,
                audioFormat.sampleRate.toInt()
            )
        }

        private fun convertAudioBytes(audioBytes: ByteArray, twoBytesData: Boolean, order: ByteOrder): ByteBuffer {
            val dest = ByteBuffer.allocateDirect(audioBytes.size)
            dest.order(ByteOrder.nativeOrder())
            val src = ByteBuffer.wrap(audioBytes)
            src.order(order)
            if (twoBytesData) {
                val destShort = dest.asShortBuffer()
                val srcShort = src.asShortBuffer()
                while (srcShort.hasRemaining()) destShort.put(srcShort.get())
            } else {
                while (src.hasRemaining()) dest.put(src.get())
            }
            dest.rewind()
            return dest
        }

    }

}