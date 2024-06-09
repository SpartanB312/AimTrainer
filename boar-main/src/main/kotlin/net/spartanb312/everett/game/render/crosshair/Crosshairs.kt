package net.spartanb312.everett.game.render.crosshair

import net.spartanb312.everett.game.render.crosshair.impls.CrosshairCircle
import net.spartanb312.everett.game.render.crosshair.impls.CrosshairCross
import net.spartanb312.everett.game.render.crosshair.impls.CrosshairCustom
import net.spartanb312.everett.game.render.crosshair.impls.CrosshairDot
import net.spartanb312.everett.game.render.crosshair.impls.gun.CrosshairBR75
import net.spartanb312.everett.game.render.crosshair.impls.gun.CrosshairM392E
import net.spartanb312.everett.game.render.crosshair.impls.gun.CrosshairMA40
import net.spartanb312.everett.game.Language.lang
import net.spartanb312.everett.utils.language.MultiText
import net.spartanb312.everett.utils.misc.DisplayEnum
import net.spartanb312.everett.utils.misc.Nameable

enum class Crosshairs(
    multiText: MultiText,
    val crosshairType: Type,
    val crosshair: Crosshair
) : Nameable, DisplayEnum {

    // Normal
    Cross("Cross".lang("十字", "十字"), Type.NORMAL, CrosshairCross),
    Circle("Circle".lang("圆圈", "圓圈"), Type.NORMAL, CrosshairCircle),
    Dot("Dot".lang("点", "點"), Type.NORMAL, CrosshairDot),
    Custom("Custom".lang("自定义", "自訂"), Type.NORMAL, CrosshairCustom),

    // Gun
    M392E("Bandit Evo".lang("Bandit Evo", "Bandit Evo"), Type.GUN, CrosshairM392E),
    BR75("BR75".lang("BR75", "BR75"), Type.GUN, CrosshairBR75),
    MA40("MA40 AR".lang("MA40 AR", "MA40 AR"), Type.GUN, CrosshairMA40);

    override val nameString: String by multiText
    override val displayName: CharSequence
        get() = nameString

    enum class Type {
        GUN,
        NORMAL
    }

}
