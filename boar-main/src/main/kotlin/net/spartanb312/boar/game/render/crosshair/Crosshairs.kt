package net.spartanb312.boar.game.render.crosshair

import net.spartanb312.boar.game.render.crosshair.impls.CrosshairCircle
import net.spartanb312.boar.game.render.crosshair.impls.CrosshairCross
import net.spartanb312.boar.game.render.crosshair.impls.CrosshairCustom
import net.spartanb312.boar.game.render.crosshair.impls.CrosshairDot
import net.spartanb312.boar.game.render.crosshair.impls.gun.CrosshairBR75
import net.spartanb312.boar.game.render.crosshair.impls.gun.CrosshairM392E
import net.spartanb312.boar.game.render.crosshair.impls.gun.CrosshairMA40
import net.spartanb312.boar.utils.misc.DisplayEnum
import net.spartanb312.boar.utils.misc.Nameable

enum class Crosshairs(
    override val nameString: String,
    val crosshairType: Type,
    val crosshair: Crosshair
) : Nameable, DisplayEnum {

    // Normal
    Cross("Cross", Type.NORMAL, CrosshairCross),
    Circle("Circle",Type.NORMAL, CrosshairCircle),
    Dot("Dot", Type.NORMAL, CrosshairDot),
    Custom("Custom", Type.NORMAL, CrosshairCustom),

    // Gun
    M392E("Bandit Evo", Type.GUN, CrosshairM392E),
    BR75("BR75", Type.GUN, CrosshairBR75),
    MA40("MA40 AR", Type.GUN, CrosshairMA40);

    override val displayName: CharSequence
        get() = nameString

    enum class Type {
        GUN,
        NORMAL
    }

}
