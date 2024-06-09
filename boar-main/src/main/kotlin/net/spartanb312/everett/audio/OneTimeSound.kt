package net.spartanb312.everett.audio

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.lwjgl.openal.AL10.AL_STOPPED

@OptIn(DelicateCoroutinesApi::class)
class OneTimeSound(private val source: Sound) {

    private val recycle = mutableListOf<Sound>()

    init {
        GlobalScope.launch(Dispatchers.IO) {
            recycle.removeIf {
                it.updateStates() // desync
                it.states == AL_STOPPED
            }
        }
    }

    fun play() {

    }

}