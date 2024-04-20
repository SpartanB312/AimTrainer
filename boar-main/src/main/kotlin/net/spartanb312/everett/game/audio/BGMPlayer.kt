package net.spartanb312.everett.game.audio

import net.spartanb312.everett.AimTrainer
import net.spartanb312.everett.audio.Sound
import net.spartanb312.everett.game.option.impls.AudioOption
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.launch.Main
import net.spartanb312.everett.utils.Logger
import net.spartanb312.everett.utils.timing.Timer

object BGMPlayer {

    private val bgmList = if (Main.fullMode) mutableListOf(
        Sound("assets/sound/background/bg1.wav"),
        Sound("assets/sound/background/bg2.wav"),
        Sound("assets/sound/background/bg3.wav"),
        Sound("assets/sound/background/bg4.wav"),
    ) else mutableListOf(Sound())

    var currentBGM = nextBGM()

    fun nextBGM(): Sound {
        val bgm = bgmList.random()
        return if (currentBGM == bgm) nextBGM()
        else {
            Logger.info("Start playing BGM ${bgm.name}")
            bgm
        }
    }

    private var volume = 0f
    private var mute = true

    fun changeSound(sound: Sound) {
        currentBGM.stop()
        currentBGM = sound
        sound.play()
    }

    val timer = Timer()

    fun onTick() {
        if (!Main.fullMode || !currentBGM.available) return
        mute = (SceneManager.inTraining && !AudioOption.bgmInGame) || !AimTrainer.isReady
        currentBGM.updateStates()

        volume += if (mute) -5 else 5
        volume = volume.coerceIn(0f..100f)

        if (volume == 0f) currentBGM.pause()
        else if (currentBGM.initial || currentBGM.paused) {
            currentBGM.play()
        }
        val actualVolume = AudioOption.music * volume / 100f
        currentBGM.setVolume(actualVolume)

        if (currentBGM.stopped) {
            changeSound(nextBGM())
        }
    }

}