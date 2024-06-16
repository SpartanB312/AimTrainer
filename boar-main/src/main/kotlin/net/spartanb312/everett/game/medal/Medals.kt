package net.spartanb312.everett.game.medal

import net.spartanb312.everett.game.render.TextureManager
import net.spartanb312.everett.graphics.texture.Texture
import net.spartanb312.everett.utils.color.ColorRGB

interface Medal {
    val str: String
    val priority: Int
    val texture: Texture?
    val level: Level

    enum class Level(val color: ColorRGB) {
        Common(ColorRGB.GREEN),// 0..99
        Rare(ColorRGB.AQUA),// 100..199
        Epic(ColorRGB.LIGHT_PURPLE),// 200..299
        Legendary(ColorRGB.DARK_RED)// 300+
    }
}

class EmptyMedal(override val str: String, val color: ColorRGB) : Medal {
    override val level = Medal.Level.Common
    override val priority = -1
    override val texture = null
}

object Perfect : Medal {
    override val str = "Perfect"
    override val level = Medal.Level.Rare
    override val priority = 100
    override val texture = TextureManager.perfect
}

object ClockStop : Medal {
    override val str = "Clock Stop"
    override val level = Medal.Level.Rare
    override val priority = 199
    override val texture = TextureManager.clockStop
}

object FlawlessVictory : Medal {
    override val str = "Flawless Victory"
    override val level = Medal.Level.Epic
    override val priority = 299
    override val texture = TextureManager.flawlessVictory
}

sealed class KillMedal(
    override val str: String,
    override val level: Medal.Level,
    override val priority: Int,
    override val texture: Texture?
) : Medal {

    data object DoubleKill : KillMedal("Double Kill", Medal.Level.Rare, 101, TextureManager.kill2)
    data object TripleKill : KillMedal("Triple Kill", Medal.Level.Epic, 201, TextureManager.kill3)
    data object OverKill : KillMedal("Overkill", Medal.Level.Legendary, 301, TextureManager.kill4)
    data object Killtacular : KillMedal("Killtacular", Medal.Level.Legendary, 302, TextureManager.kill5)
    data object Killtrocity : KillMedal("Killtrocity", Medal.Level.Legendary, 303, TextureManager.kill6)
    data object Killamanjaro : KillMedal("Killamanjaro", Medal.Level.Legendary, 304, TextureManager.kill7)
    data object Killtastrophe : KillMedal("Killtastrophe", Medal.Level.Legendary, 305, TextureManager.kill8)
    data object Killpocalypse : KillMedal("Killpocalypse", Medal.Level.Legendary, 306, TextureManager.kill9)
    data object Killionaire : KillMedal("Killionaire", Medal.Level.Legendary, 307, TextureManager.kill10)

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