package net.spartanb312.everett.game.medal

import net.spartanb312.everett.game.render.TextureManager
import net.spartanb312.everett.graphics.texture.Texture
import net.spartanb312.everett.utils.color.ColorRGB

interface Medal {
    val str: String
    val texture: Texture?
}

class EmptyMedal(override val str: String, val color: ColorRGB) : Medal {
    override val texture = null
}

object Perfect : Medal {
    override val str = "Perfect"
    override val texture = TextureManager.perfect
}

sealed class KillMedal(override val str: String, override val texture: Texture?) : Medal {

    data object DoubleKill : KillMedal("Double Kill", TextureManager.kill2)
    data object TripleKill : KillMedal("Triple Kill", TextureManager.kill3)
    data object OverKill : KillMedal("Overkill", TextureManager.kill4)
    data object Killtacular : KillMedal("Killtacular", TextureManager.kill5)
    data object Killtrocity : KillMedal("Killtrocity", TextureManager.kill6)
    data object Killamanjaro : KillMedal("Killamanjaro", TextureManager.kill7)
    data object Killtastrophe : KillMedal("Killtastrophe", TextureManager.kill8)
    data object Killpocalypse : KillMedal("Killpocalypse", TextureManager.kill9)
    data object Killionaire : KillMedal("Killionaire", TextureManager.kill10)

    companion object {
        fun getMedal(killCount: Int): KillMedal? = when (killCount) {
            2 -> DoubleKill
            3 -> TripleKill
            4 -> OverKill
            5 -> Killtacular
            6 -> Killtrocity
            7 -> Killamanjaro
            8 -> Killtastrophe
            9 -> Killpocalypse
            10 -> Killionaire
            else -> null
        }
    }

}