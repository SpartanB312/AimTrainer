package net.spartanb312.everett.game.audio.notebox

import net.spartanb312.everett.audio.Sound
import net.spartanb312.everett.audio.decoder.WaveData
import net.spartanb312.everett.utils.ResourceHelper
import kotlin.math.pow

// C4 -> [C1, C7)
open class NotePlayer(
    val name: String,
    soundPath: String,
    pitch: String = "C4",
    volume: Float = 1.0f
) {

    private val waveData = WaveData.create(ResourceHelper.getResourceStream(soundPath)!!)
    private val standardPitchIndex = Notes.getPitchIndex(pitch)
    private val gapRate = 2.0.pow(1.0 / 12.0).toFloat()
    val sounds = mutableListOf<Sound>()

    init {
        // positive
        val positiveCount = 72 - standardPitchIndex
        val positive = mutableListOf<Float>()
        var r = 1f
        repeat(positiveCount) {
            positive.add(r)
            r *= gapRate
        }
        // negative
        val negativeCount = standardPitchIndex
        val negative = mutableListOf<Float>()
        if (negativeCount >= 0) {
            r = 1f / gapRate
            repeat(negativeCount) {
                negative.add(r)
                r /= gapRate
            }
        }
        // combine
        val rates = mutableListOf<Float>()
        rates.addAll(negative.reversed())
        rates.addAll(positive)
        // generate sounds
        for ((index, rate) in rates.withIndex()) {
            val s = Sound({ waveData }, "$name[${Notes.getIndexedPitch(index)}]") {
                it.setPitch(rate, true)
                it.setVolume(volume, true)
            }
            sounds.add(s)
        }
    }

    fun setVolume(volume: Float, force: Boolean = false) = sounds.forEach { it.setVolume(volume, force) }

}