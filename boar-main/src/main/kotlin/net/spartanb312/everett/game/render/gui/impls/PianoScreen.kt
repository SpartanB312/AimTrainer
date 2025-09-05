package net.spartanb312.everett.game.render.gui.impls

import net.spartanb312.everett.AimTrainer
import net.spartanb312.everett.game.audio.noteplayer.MidiPlayer
import net.spartanb312.everett.game.render.Background
import net.spartanb312.everett.game.render.FontRendererROG
import net.spartanb312.everett.game.render.gui.GuiScreen
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.hud.PianoHUD
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.font.drawColoredString
import org.lwjgl.glfw.GLFW
import kotlin.math.min

object PianoScreen : GuiScreen() {

    override fun onRender(mouseX: Double, mouseY: Double) {
        Background.update(1f)
        Background.renderBackground(mouseX, mouseY)
        val scale = min(RS.widthScale, RS.heightScale)
        val width = FontRendererROG.getWidth("Synthetic Piano", scale)
        val height = FontRendererROG.getHeight(scale)
        FontRendererROG.drawColoredString(
            "Synthetic Piano",
            RS.centerXF - width / 2f,
            RS.centerYF * 0.9f - height / 2f,
            saturation = 0.8f,
            shadowDepth = 2f * scale,
            scale = scale,
            alpha = 0.75f,
        )
        PianoHUD.render()
    }

    override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean {
        when (keyCode) {
            GLFW.GLFW_KEY_F8 -> {
                MidiPlayer.playSong(AimTrainer.midi)
                return true
            }

            GLFW.GLFW_KEY_ESCAPE -> {
                Render2DManager.popScreen()
                return true
            }

            else -> return false
        }
    }

    override fun onClosed() {
        MidiPlayer.stop()
    }

}