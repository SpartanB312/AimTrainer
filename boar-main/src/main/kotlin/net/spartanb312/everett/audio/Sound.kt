package net.spartanb312.everett.audio

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.spartanb312.everett.audio.decoder.MP3Data
import net.spartanb312.everett.audio.decoder.SoundData
import net.spartanb312.everett.audio.decoder.WaveData
import net.spartanb312.everett.utils.Logger
import net.spartanb312.everett.utils.ResourceHelper
import org.lwjgl.openal.AL10.*

class Sound(
    private val soundDataGetter: () -> SoundData?,
    val name: String,
    private val afterInit: ((Sound) -> Unit)? = null
) {

    constructor() : this({ null }, "dummy")

    constructor(path: String, afterInit: ((Sound) -> Unit)? = null) : this(
        {
            when (val format = path.lowercase().substringAfterLast(".")) {
                "wav" -> WaveData.create(ResourceHelper.getResourceStream(path)!!)
                "ogg" -> TODO()
                "mp3" -> MP3Data.create(ResourceHelper.getResourceStream(path)!!, path)
                else -> {
                    Logger.error("Unsupported format $format")
                    null
                }
            }
        },
        path,//.substringAfterLast("/").substringAfterLast("\\").substringBeforeLast(".")
        afterInit
    )

    var sourceID = 0
    var bufferID = 0
    var available = false; private set
    var states = AL_INITIAL
    val initial get() = states == AL_INITIAL
    val playing get() = states == AL_PLAYING
    val paused get() = states == AL_PAUSED
    val stopped get() = states == AL_STOPPED
    val soundData by lazy { soundDataGetter.invoke() }

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

        AudioSystem.launch(Dispatchers.IO) {
            soundData?.let {
                AudioSystem.runOnAudioThread {
                    alBufferData(bufferID, it.format, it.data, it.sampleRate)
                    alSourcei(sourceID, AL_BUFFER, bufferID)
                    available = true
                    afterInit?.invoke(this@Sound)
                }
            }
        }
    }

    fun play(force: Boolean = false): Sound {
        if (force || Thread.currentThread() == AudioSystem) {
            if (available) {
                if (alGetSourcei(sourceID, AL_SOURCE_STATE) != AL_PLAYING) alSourcePlay(sourceID)
            } else Logger.error("[AudioSystem] Unable to to play $name")
        } else AudioSystem.runOnAudioThread { play(true) }
        return this
    }

    fun pause(force: Boolean = false): Sound {
        if (force || Thread.currentThread() == AudioSystem) {
            if (available) {
                if (alGetSourcei(sourceID, AL_SOURCE_STATE) == AL_PLAYING) alSourcePause(sourceID)
            } else Logger.error("[AudioSystem] Unable to pause $name")
        } else AudioSystem.runOnAudioThread { pause(true) }
        return this
    }

    fun stop(force: Boolean = false): Sound {
        if (force || Thread.currentThread() == AudioSystem) {
            if (available) alSourceStop(sourceID)
            else Logger.error("[AudioSystem] Unable to stop $name")
        } else AudioSystem.runOnAudioThread { stop(true) }
        return this
    }

    fun setVolume(volume: Float, force: Boolean = false): Sound {
        if (force || Thread.currentThread() == AudioSystem) {
            if (available) alSourcef(sourceID, AL_GAIN, volume.coerceIn(0f..1f))
            else Logger.error("[AudioSystem] Unable to set volume to $volume for $name")
        } else AudioSystem.runOnAudioThread { setVolume(volume, true) }
        return this
    }

    fun setPitch(pitch: Float, force: Boolean = false) :Sound{
        if (force || Thread.currentThread() == AudioSystem) {
            if (available) alSourcef(sourceID, AL_PITCH, pitch.coerceAtLeast(0f))
            else Logger.error("[AudioSystem] Unable to set pitch to $pitch for $name")
        } else AudioSystem.runOnAudioThread { setPitch(pitch, true) }
        return this
    }

    fun clone(): Sound? {
        return if (available) {
            Sound(soundDataGetter, name)
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