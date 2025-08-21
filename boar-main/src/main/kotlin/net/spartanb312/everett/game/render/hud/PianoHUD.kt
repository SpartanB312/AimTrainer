package net.spartanb312.everett.game.render.hud

import net.spartanb312.everett.game.audio.notebox.Piano
import net.spartanb312.everett.game.audio.noteplayer.MidiPlayer
import net.spartanb312.everett.game.audio.noteplayer.Note
import net.spartanb312.everett.game.audio.noteplayer.Song
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.graphics.event.EngineLoopEvent
import net.spartanb312.everett.graphics.matrix.scope
import net.spartanb312.everett.graphics.matrix.translatef
import net.spartanb312.everett.utils.Quad
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.event.ListenerOwner
import net.spartanb312.everett.utils.event.listener
import net.spartanb312.everett.utils.timing.Timer
import org.lwjgl.glfw.GLFW

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
        var index = 0
        for ((octave, _) in (0..8).withIndex()) {
            keys.add(Key(index++, octave * 7f + 0f, false)) // C
            keys.add(Key(index++, octave * 7f + 0.6f, true)) // C#
            keys.add(Key(index++, octave * 7f + 1f, false)) // D
            keys.add(Key(index++, octave * 7f + 1.6f, true)) // D#
            keys.add(Key(index++, octave * 7f + 2f, false)) // E
            keys.add(Key(index++, octave * 7f + 3f, false)) // F
            keys.add(Key(index++, octave * 7f + 3.6f, true)) // F#
            keys.add(Key(index++, octave * 7f + 4f, false)) // G
            keys.add(Key(index++, octave * 7f + 4.6f, true)) // G#
            keys.add(Key(index++, octave * 7f + 5f, false)) // A
            keys.add(Key(index++, octave * 7f + 5.6f, true)) // A#
            keys.add(Key(index++, octave * 7f + 6f, false)) // B
        }
    }

    fun render() {
        val unitWidth = RS.widthF / 63f
        val unitHeight = unitWidth * 5f
        val lineWidth = 1f
        // mouse check
        if (GLFW.glfwGetMouseButton(RS.window, GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS) {
            pressCheck(RS.mouseXF, RS.mouseYF - RS.heightF + unitHeight)
        } else releaseCheck()
        // tiles
        val tileRenderRegionHeight = RS.heightF - unitHeight
        val tickRange = 500
        val heightPerTick = tileRenderRegionHeight / tickRange.toFloat()
        for (tile in tiles) {
            // skip invisible tiles
            if (tile.startTick > playTick + tickRange || tile.endTick < playTick) continue
            val tileWidth = if (keys[tile.note.index].isBlack) 0.8f * unitWidth else unitWidth * 0.95f
            val startX = unitWidth * tile.xUnitOffset
            val startY = (playTick + 500 - tile.endTick) * heightPerTick
            val endX = startX + tileWidth
            val endY = (playTick + 500 - tile.startTick) * heightPerTick
            RenderUtils.drawRect(startX, startY, endX, endY, tile.note.color.alpha(192))
        }
        // stroke
        RS.matrixLayer.scope {
            translatef(0f, RS.heightF - unitHeight, 0f)
            RenderUtils.drawRect(0f, 0f, unitWidth * 70f, unitHeight, ColorRGB.WHITE)
            keys.forEach { if (!it.isBlack) it.render(unitWidth, unitHeight, lineWidth) }
            keys.forEach { if (it.isBlack) it.render(unitWidth, unitHeight, lineWidth) }
        }
    }

    private val clickAreaCache = Array(108) { Pair(Quad(0f, 0f, 0f, 0f), true) }

    class Key(
        val index: Int,
        val xUnitOffset: Float,
        val isBlack: Boolean,
    ) {
        var pressed = false
        var alpha = 0f
        var color = ColorRGB.RED
        fun render(unitWidth: Float, unitHeight: Float, lineWidth: Float) {
            val xPos = xUnitOffset * unitWidth
            if (isBlack) {
                RenderUtils.drawRect(
                    xPos,
                    0f,
                    xPos + unitWidth * 0.8f,
                    unitHeight * 0.6f,
                    ColorRGB.BLACK
                )
                clickAreaCache[index] = Quad(xPos, 0f, xPos + unitWidth * 0.8f, unitHeight * 0.6f) to true
            } else {
                RenderUtils.drawRectOutline(
                    xPos,
                    0f,
                    xPos + unitWidth,
                    unitHeight,
                    lineWidth,
                    ColorRGB.BLACK
                )
                clickAreaCache[index] = Quad(xPos, 0f, xPos + unitWidth, unitHeight) to false
            }
            if (alpha > 0f) {
                if (isBlack) RenderUtils.drawRect(
                    xPos,
                    0f,
                    xPos + unitWidth * 0.8f,
                    unitHeight * 0.6f,
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
        song.tracks.forEach { tr ->
            tr.notes.values().forEach {
                tiles.add(Tile(it, keys[it.index].xUnitOffset, it.end - it.start))
            }
        }
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

    private val clickStarts = IntArray(108) { 0 }
    private val lastClickTime = LongArray(108) { 0L }

    private fun pressCheck(mouseX: Float, mouseY: Float) {
        var pressed = false
        // black
        for ((index, key) in clickAreaCache.withIndex()) {
            if (key.second) {
                if (key.first.covered(mouseX, mouseY) && !pressed) {
                    if (clickStarts[index] == 0) {
                        Piano.sounds[index].stop().play()
                        press(index, ColorRGB.BLUE)
                    }
                    clickStarts[index] = 1
                    lastClickTime[index] = System.currentTimeMillis()
                    pressed = true
                } else if (clickStarts[index] == 1) {
                    if (System.currentTimeMillis() - lastClickTime[index] >= 250) {
                        lastClickTime[index] = System.currentTimeMillis()
                        Piano.sounds[index].stop()
                        release(index)
                        clickStarts[index] = 0
                    }
                }
            }
        }
        // white
        for ((index, key) in clickAreaCache.withIndex()) {
            if (!key.second) {
                if (key.first.covered(mouseX, mouseY) && !pressed) {
                    if (clickStarts[index] == 0) {
                        Piano.sounds[index].stop().play()
                        press(index, ColorRGB.BLUE)
                    }
                    clickStarts[index] = 1
                    lastClickTime[index] = System.currentTimeMillis()
                    pressed = true
                } else if (clickStarts[index] == 1) {
                    if (System.currentTimeMillis() - lastClickTime[index] >= 250) {
                        lastClickTime[index] = System.currentTimeMillis()
                        Piano.sounds[index].stop()
                        release(index)
                        clickStarts[index] = 0
                    }
                }
            }
        }
    }

    private fun releaseCheck() {
        for (index in 0..107) {
            if (clickStarts[index] == 1) {
                if (System.currentTimeMillis() - lastClickTime[index] >= 250) {
                    lastClickTime[index] = System.currentTimeMillis()
                    Piano.sounds[index].stop()
                    release(index)
                    clickStarts[index] = 0
                }
            }
        }
    }

    private fun Quad<Float, Float, Float, Float>.covered(x: Float, y: Float): Boolean {
        return x >= first && x <= third && y >= second && y <= forth
    }

}