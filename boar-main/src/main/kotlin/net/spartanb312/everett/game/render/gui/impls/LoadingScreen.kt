package net.spartanb312.everett.game.render.gui.impls

import net.spartanb312.everett.AimTrainer
import net.spartanb312.everett.game.render.Background.drawBackground
import net.spartanb312.everett.game.render.FontRendererASCII
import net.spartanb312.everett.game.render.TextureManager
import net.spartanb312.everett.game.render.gui.GuiScreen
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.graphics.texture.MipmapTexture
import net.spartanb312.everett.utils.color.ColorRGB

object LoadingScreen : GuiScreen() {

    private val bg = MipmapTexture("assets/texture/loading_bg.jpg")

    override fun onRender(mouseX: Double, mouseY: Double) {
        if (TextureManager.loadedCount == TextureManager.totalCount) {
            Render2DManager.displayScreen(MainMenuScreen)
            AimTrainer.isReady = true
        }
        bg.drawBackground(mouseX, mouseY, 0f)
        val rate = TextureManager.loadedCount / TextureManager.totalCount.toFloat()
        val scale = RS.generalScale
        RenderUtils.drawRect(
            RS.widthF * 0.3f,
            RS.heightF * 0.7f,
            RS.widthF * 0.7f,
            RS.heightF * 0.7f + 40f * scale,
            ColorRGB.GRAY.alpha(64)
        )
        RenderUtils.drawRect(
            RS.widthF * 0.3f,
            RS.heightF * 0.7f,
            RS.widthF * (0.3f + 0.4f * rate),
            RS.heightF * 0.7f + 40f * scale,
            ColorRGB(30, 90, 255).alpha(128)
        )
        RenderUtils.drawRectOutline(
            RS.widthF * 0.3f,
            RS.heightF * 0.7f,
            RS.widthF * 0.7f,
            RS.heightF * 0.7f + 40f * scale,
            2f * scale,
            ColorRGB.WHITE
        )
        FontRendererASCII.drawCenteredString(
            "${TextureManager.loadedCount}/${TextureManager.totalCount}",
            RS.centerXF,
            RS.heightF * 0.7f + 20f * scale,
            scale = scale
        )
    }

}