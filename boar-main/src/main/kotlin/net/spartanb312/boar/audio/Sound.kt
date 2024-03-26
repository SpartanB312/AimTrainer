package net.spartanb312.boar.audio

import net.spartanb312.boar.audio.decoder.SoundData
import net.spartanb312.boar.audio.decoder.WaveData
import net.spartanb312.boar.utils.Logger
import net.spartanb312.boar.utils.ResourceHelper
import org.lwjgl.openal.AL10.*

class Sound(private val soundData: SoundData?) {

    constructor(path: String) : this(
        when (val format = path.lowercase().substringAfterLast(".")) {
            "wav" -> WaveData.create(ResourceHelper.getResourceStream(path)!!)
            "ogg" -> TODO()
            "mp3" -> TODO()
            else -> {
                Logger.error("Unsupported format $format")
                null
            }
        }
    )

    var sourceID = 0
    var bufferID = 0
    var available = false; private set
    var states = AL_INITIAL
    val initial get() = states == AL_INITIAL
    val playing get() = states == AL_PLAYING
    val paused get() = states == AL_PAUSED
    val stopped get() = states == AL_STOPPED

    fun updateStates() = AudioSystem.runOnAudioThread {
        states = alGetSourcei(sourceID, AL_SOURCE_STATE)
    }

    init {
        if (Thread.currentThread() == AudioSystem) init()
        else AudioSystem.init(this)
    }

    fun init() {
        if (Thread.currentThread() != AudioSystem) throw Exception("Not in AudioThread")
        sourceID = alGenSources()
        bufferID = alGenBuffers()
        soundData?.let {
            alBufferData(bufferID, soundData.format, soundData.data, soundData.sampleRate)
            alSourcei(sourceID, AL_BUFFER, bufferID)
            available = true
        }
    }

    fun play(force: Boolean = false) {
        if (force || Thread.currentThread() == AudioSystem) {
            if (available) {
                if (alGetSourcei(sourceID, AL_SOURCE_STATE) != AL_PLAYING) alSourcePlay(sourceID)
            } else Logger.error("Sound unavailable to play")
        } else AudioSystem.runOnAudioThread { play(true) }
    }

    fun pause(force: Boolean = false) {
        if (force || Thread.currentThread() == AudioSystem) {
            if (available) {
                if (alGetSourcei(sourceID, AL_SOURCE_STATE) == AL_PLAYING) alSourcePause(sourceID)
            } else Logger.error("Sound unavailable to pause")
        } else AudioSystem.runOnAudioThread { pause(true) }
    }

    fun stop(force: Boolean = false) {
        if (force || Thread.currentThread() == AudioSystem) {
            if (available) alSourceStop(sourceID)
            else Logger.error("Sound unavailable to stop")
        } else AudioSystem.runOnAudioThread { stop(true) }
    }

    fun setVolume(volume: Float, force: Boolean = false) {
        if (force || Thread.currentThread() == AudioSystem) {
            if (available) alSourcef(sourceID, AL_GAIN, volume.coerceIn(0f..1f))
            else Logger.error("Sound unavailable")
        } else AudioSystem.runOnAudioThread { setVolume(volume, true) }
    }

    fun clone(): Sound? {
        return if (available) {
            Sound(soundData)
        } else {
            Logger.error("Sound unavailable")
            null
        }
    }

    fun delete() {
        soundData?.dispose()
        available = false
        alDeleteBuffers(bufferID)
        alDeleteSources(sourceID)
    }

}