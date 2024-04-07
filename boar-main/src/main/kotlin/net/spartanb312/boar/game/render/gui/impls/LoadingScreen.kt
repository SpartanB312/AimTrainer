package net.spartanb312.boar.game.render.gui.impls

import net.spartanb312.boar.AimTrainer
import net.spartanb312.boar.game.render.Background.drawBackground
import net.spartanb312.boar.game.render.FontRendererASCII
import net.spartanb312.boar.game.render.TextureManager
import net.spartanb312.boar.game.render.gui.GuiScreen
import net.spartanb312.boar.game.render.gui.Render2DManager
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.graphics.texture.MipmapTexture
import net.spartanb312.boar.utils.color.ColorRGB

object LoadingScreen : GuiScreen() {

    private val bg = MipmapTexture("assets/texture/loading_bg.jpg")

    override fun onRender(mouseX: Double, mouseY: Double) {
        if (TextureManager.loadedCount == TextureManager.totalCount) {
            Render2DManager.displayScreen(MainMenuScreen)
            AimTrainer.isReady = true
        }
        bg.drawBackground(mouseX, mouseY, 0f)
        val rate = TextureManager.loadedCount / TextureManager.totalCount.toFloat()
        RenderUtils.drawRect(
            RS.widthF * 0.3f,
            RS.heightF * 0.7f,
            RS.widthF * 0.7f,
            RS.heightF * 0.7f + 40f,
            ColorRGB.GRAY.alpha(64)
        )
        RenderUtils.drawRect(
            RS.widthF * 0.3f,
            RS.heightF * 0.7f,
            RS.widthF * (0.3f + 0.4f * rate),
            RS.heightF * 0.7f + 40f,
            ColorRGB(30, 90, 255).alpha(128)
        )
        RenderUtils.drawRectOutline(
            RS.widthF * 0.3f,
            RS.heightF * 0.7f,
            RS.widthF * 0.7f,
            RS.heightF * 0.7f + 40f,
            2f,
            ColorRGB.WHITE
        )
        FontRendererASCII.drawCenteredString(
            "${TextureManager.loadedCount}/${TextureManager.totalCount}",
            RS.centerXF,
            RS.heightF * 0.7f + 20f
        )
    }

}