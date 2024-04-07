package net.spartanb312.boar.audio

import kotlinx.coroutines.CoroutineScope
import net.spartanb312.boar.graphics.RenderSystem
import net.spartanb312.boar.utils.Logger
import net.spartanb312.boar.utils.misc.NULL
import net.spartanb312.boar.utils.thread.newCoroutineScope
import org.lwjgl.openal.AL
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC11.*
import org.lwjgl.openal.ALUtil
import org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext
import org.lwjgl.system.MemoryUtil
import java.nio.IntBuffer
import java.util.concurrent.LinkedBlockingQueue

object AudioSystem : Thread("AudioThread"), CoroutineScope by newCoroutineScope(
    Runtime.getRuntime().availableProcessors(),
    "AudioLoader"
) {

    var alcDevice = 0L; private set
    val devices = mutableMapOf<Int, String>()
    private val playQueue = LinkedBlockingQueue<Pair<Long, Sound>>() // Playtime, Sound
    private val delayPlay = mutableListOf<Pair<Long, Sound>>() // Playtime, Sound
    private val initialQueue = LinkedBlockingQueue<Sound>()
    private val threadTask = LinkedBlockingQueue<Runnable>()

    override fun run() {
        init()
        while (RenderSystem.isAlive) {
            loop()
        }
        alcCloseDevice(alcDevice)
    }

    private fun init() {
        alcDevice = alcOpenDevice(null as String?)
        val capabilities = ALC.createCapabilities(alcDevice)
        if (!capabilities.OpenALC10) throw Exception("Unsupported device")

        if (capabilities.OpenALC11) {
            val devices = ALUtil.getStringList(NULL, ALC_ALL_DEVICES_SPECIFIER)
            devices?.forEachIndexed { index, device ->
                this.devices[index] = device.substringAfter("on ")
            }
        }

        val context = alcCreateContext(alcDevice, null as IntBuffer?)
        val useTLC = capabilities.ALC_EXT_thread_local_context && alcSetThreadContext(context)
        if (!useTLC && !alcMakeContextCurrent(context)) throw IllegalStateException()
        AL.createCapabilities(capabilities) { MemoryUtil.memCallocPointer(it) }
        Logger.debug("ALC_FREQUENCY     : " + alcGetInteger(alcDevice, ALC_FREQUENCY) + "Hz")
        Logger.debug("ALC_REFRESH       : " + alcGetInteger(alcDevice, ALC_REFRESH) + "Hz")
        Logger.debug("ALC_SYNC          : " + (alcGetInteger(alcDevice, ALC_SYNC) == ALC_TRUE))
        Logger.debug("ALC_MONO_SOURCES  : " + alcGetInteger(alcDevice, ALC_MONO_SOURCES))
        Logger.debug("ALC_STEREO_SOURCES: " + alcGetInteger(alcDevice, ALC_STEREO_SOURCES))
    }

    private fun loop() {
        while (true) {
            val task = threadTask.poll()
            if (task != null) task.run()
            else break
        }
        while (true) {
            val init = initialQueue.poll()
            if (init != null) {
                if (!init.available) {
                    init.init()
                }
            } else break
        }
        while (true) {
            val play = playQueue.poll()
            if (play != null) {
                if (System.currentTimeMillis() >= play.first) {
                    play.second.play(true)
                } else delayPlay.add(play)
            } else break
        }
        delayPlay.removeIf {
            val flag = System.currentTimeMillis() >= it.first
            if (flag) it.second.play()
            flag
        }
        sleep(1)
    }

    fun init(sound: Sound) {
        initialQueue.put(sound)
    }

    fun play(sound: Sound, delay: Int = 0) {
        playQueue.put((System.currentTimeMillis() + delay) to sound)
    }

    fun playClone(sound: Sound, delay: Int = 0) = play(sound.clone()!!, delay)

    fun runOnAudioThread(task: Runnable) {
        threadTask.put(task)
    }

}