package net.spartanb312.everett.audio.decoder

import java.nio.ByteBuffer

interface SoundData {
    val data: ByteBuffer
    val format: Int
    val sampleRate: Int

    fun dispose() {
        data.clear()
    }
}