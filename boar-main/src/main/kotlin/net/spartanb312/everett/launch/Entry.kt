package net.spartanb312.everett.launch

import net.spartanb312.everett.AimTrainer
import net.spartanb312.everett.game.Language.lang
import net.spartanb312.everett.graphics.RenderSystem
import net.spartanb312.everett.utils.language.MultiText
import net.spartanb312.everett.utils.misc.DisplayEnum

// Only for debug
fun main(): Unit = Main.main(arrayOf("-DevMode"))

/**
 * The entry of the game
 */
object Entry {
    init {
       println( getModes<SensBase>().joinToString { it })
        RenderSystem.launch(AimTrainer::class.java, 1920, 1080)
    }
}

inline fun <reified E : Enum<E>> getModes(): List<String> {
    return buildList {
        enumValues<E>().forEach {
            if (it is DisplayEnum) add(it.displayString)
            else add(it.name)
        }
    }
}

enum class SensBase(val multiplier: Double, multiText: MultiText) : DisplayEnum {
    HaloInfinite(0.0371248537, "Halo Infinite".lang("光环无限", "光暈無限")),
    ApexLegends(0.0371248537 * 0.9777777777777779, "Apex Legends".lang("Apex英雄", "Apex英雄")),
    Valorant(0.0371248537 * 3.110777550058213, "Valorant".lang("无畏契约", "特戰英豪"));

    override val displayName by multiText
}