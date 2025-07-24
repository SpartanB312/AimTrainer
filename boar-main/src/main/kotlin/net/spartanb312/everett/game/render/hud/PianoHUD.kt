package net.spartanb312.everett.game.render.hud

import net.spartanb312.everett.game.audio.noteplayer.MidiPlayer
import net.spartanb312.everett.game.audio.noteplayer.Note
import net.spartanb312.everett.game.audio.noteplayer.Song
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.graphics.event.EngineLoopEvent
import net.spartanb312.everett.graphics.matrix.scope
import net.spartanb312.everett.graphics.matrix.translatef
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.event.ListenerOwner
import net.spartanb312.everett.utils.event.listener
import net.spartanb312.everett.utils.timing.Timer

object PianoHUD : ListenerOwner() {

    private val keys = mutableListOf<Key>()
    private val tickTimer = Timer()

    val playTick get() = MidiPlayer.timer

    init {
        listener<EngineLoopEvent.Loop.Pre> {
            tickTimer.tps(20) {
                keys.forEach {
                    if (!it.pressed) {
                        it.alpha -= 0.1f
                        if (it.alpha < 0f) it.alpha = 0f
                    }
                }
            }
        }
        subscribe()
    }

    init {
        for ((octave, _) in (0..9).withIndex()) {
            keys.add(Key(octave * 7f + 0f, false)) // C
            keys.add(Key(octave * 7f + 0.55f, true)) // C#
            keys.add(Key(octave * 7f + 1f, false)) // D
            keys.add(Key(octave * 7f + 1.55f, true)) // D#
            keys.add(Key(octave * 7f + 2f, false)) // E
            keys.add(Key(octave * 7f + 3f, false)) // F
            keys.add(Key(octave * 7f + 3.55f, true)) // F#
            keys.add(Key(octave * 7f + 4f, false)) // G
            keys.add(Key(octave * 7f + 4.55f, true)) // G#
            keys.add(Key(octave * 7f + 5f, false)) // A
            keys.add(Key(octave * 7f + 5.55f, true)) // A#
            keys.add(Key(octave * 7f + 6f, false)) // B
        }
    }

    fun render() {
        val unitWidth = RS.widthF / 70f
        val unitHeight = unitWidth * 5f
        val lineWidth = 1f
        // tiles
        val tileRenderRegionHeight = RS.heightF - unitHeight
        val tickRange = 500
        val heightPerTick = tileRenderRegionHeight / tickRange.toFloat()
        for (tile in tiles) {
            // skip invisible tiles
            if (tile.startTick > playTick + tickRange || tile.endTick < playTick) continue
            val tileWidth = if (keys[tile.note.index].isBlack) 0.9f * unitWidth else unitWidth * 0.95f
            val startX = unitWidth * tile.xUnitOffset
            val startY = (playTick + 500 - tile.endTick) * heightPerTick
            val endX = startX + tileWidth
            val endY = (playTick + 500 - tile.startTick) * heightPerTick
            RenderUtils.drawRect(startX, startY, endX, endY, tile.note.color)
        }
        // stroke
        RS.matrixLayer.scope {
            translatef(0f, RS.heightF - unitHeight, 0f)
            RenderUtils.drawRect(0f, 0f, unitWidth * 70f, unitHeight, ColorRGB.WHITE)
            keys.forEach { if (!it.isBlack) it.render(unitWidth, unitHeight, lineWidth) }
            keys.forEach { if (it.isBlack) it.render(unitWidth, unitHeight, lineWidth) }
        }
    }

    class Key(
        val xUnitOffset: Float,
        val isBlack: Boolean,
    ) {
        var pressed = false
        var alpha = 0f
        var color = ColorRGB.RED
        fun render(unitWidth: Float, unitHeight: Float, lineWidth: Float) {
            val xPos = xUnitOffset * unitWidth
            if (isBlack) RenderUtils.drawRect(
                xPos,
                0f,
                xPos + unitWidth * 0.9f,
                unitHeight * 0.66f,
                ColorRGB.BLACK
            ) else RenderUtils.drawRectOutline(
                xPos,
                0f,
                xPos + unitWidth,
                unitHeight,
                lineWidth,
                ColorRGB.BLACK
            )
            if (alpha > 0f) {
                if (isBlack) RenderUtils.drawRect(
                    xPos,
                    0f,
                    xPos + unitWidth * 0.9f,
                    unitHeight * 0.66f,
                    color.alpha((255 * alpha).toInt())
                ) else RenderUtils.drawRect(
                    xPos,
                    0f,
                    xPos + unitWidth,
                    unitHeight,
                    color.alpha((255 * alpha).toInt())
                )
            }
        }
    }

    fun press(keyIndex: Int, colorRGB: ColorRGB) {
        keys[keyIndex].pressed = true
        keys[keyIndex].alpha = 1f
        keys[keyIndex].color = colorRGB
    }

    fun release(keyIndex: Int) {
        keys[keyIndex].pressed = false
    }

    // tiles
    private val tiles = mutableListOf<Tile>()

    fun generateTilesForSong(song: Song) {
        tiles.clear()
        song.notes.values().forEach { tiles.add(Tile(it, keys[it.index].xUnitOffset, it.end - it.start)) }
    }

    fun stop() = tiles.clear()

    class Tile(
        val note: Note,
        val xUnitOffset: Float,
        val tickElapse: Int,
    ) {
        val startTick = note.start
        val endTick = note.end
    }

}