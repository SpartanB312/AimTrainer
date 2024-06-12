package net.spartanb312.everett.game.render

import net.spartanb312.everett.game.render.TextureManager.lazyTexture
import net.spartanb312.everett.graphics.texture.drawTexture
import net.spartanb312.everett.utils.color.ColorRGB

object NameplatesRenderer {

    sealed class Plate {
        data object TheMaw : Plate() {
            private val texture = lazyTexture("assets/texture/nameplate/HTMCC_Nameplate_TheMaw.png")
            override fun onRender(startX: Float, startY: Float, endX: Float, endY: Float) {
                texture.drawTexture(startX, startY, endX, endY)
            }
        }

        open class Skull(private val color: ColorRGB) : Plate() {
            private val texture = lazyTexture("assets/texture/nameplate/HTMCC_Nameplate_Skulls.png")
            override fun onRender(startX: Float, startY: Float, endX: Float, endY: Float) {
                texture.drawTexture(startX, startY, endX, endY, colorRGB = color.alpha(192))
            }
        }

        data object BlueSkull : Skull(ColorRGB(135 ,206 ,250))

        abstract fun onRender(startX: Float, startY: Float, endX: Float, endY: Float)
    }

    private val nameplates = mutableListOf(
        Plate.BlueSkull,
        Plate.TheMaw
    )

    val currentNameplate = nameplates[0]

}