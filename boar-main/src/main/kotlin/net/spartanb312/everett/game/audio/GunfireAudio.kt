package net.spartanb312.everett.game.audio

import net.spartanb312.everett.audio.AudioSystem
import net.spartanb312.everett.audio.Sound
import net.spartanb312.everett.game.option.impls.AudioOption
import net.spartanb312.everett.utils.math.ConvergeUtil.converge
import org.lwjgl.openal.AL10.alSourcePlay

object GunfireAudio {

    private val gunSounds = mutableListOf<Sound>()
    private val banditFire = Sound("assets/sound/bandit_fire.wav").also { gunSounds.add(it) }

    fun playBandit() {
        if (banditFire.available) {
            AudioSystem.runOnAudioThread {
                alSourcePlay(banditFire.sourceID)
            }
        }
    }

    private var volume = 100f

    fun onTick() {
        // Set volume
        val lastVolume = volume
        val targetVolume = AudioOption.hitEffect * 100f
        volume = volume.converge(targetVolume, 0.1f)
        if (volume != lastVolume) {
            gunSounds.forEach { it.setVolume(volume / 100f) }
        }
    }

}