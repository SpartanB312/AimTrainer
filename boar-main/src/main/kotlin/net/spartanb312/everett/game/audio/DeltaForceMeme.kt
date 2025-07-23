package net.spartanb312.everett.game.audio

import net.spartanb312.everett.AimTrainer
import net.spartanb312.everett.game.audio.notebox.Harp
import net.spartanb312.everett.game.audio.notebox.Piano

object DeltaForceMeme {

    private val indices = arrayOf(12, 12, 7, 8)

    fun play() {
        indices.forEachIndexed { index, i ->
            AimTrainer.taskManager.runLater(500 + index * 300) {
                Piano.sounds[35 + i].stop().play()
                Harp.sounds[35 + i].stop().play()
            }
        }

    }

}