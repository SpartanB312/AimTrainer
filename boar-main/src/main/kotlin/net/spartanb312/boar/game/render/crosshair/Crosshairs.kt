package net.spartanb312.boar.game.render.crosshair

import net.spartanb312.boar.game.render.crosshair.impls.CrosshairCross
import net.spartanb312.boar.game.render.crosshair.impls.gun.CrosshairBR75
import net.spartanb312.boar.game.render.crosshair.impls.gun.CrosshairBanditEvo
import net.spartanb312.boar.game.render.crosshair.impls.gun.CrosshairMA40
import net.spartanb312.boar.utils.misc.Nameable

enum class Crosshairs(
    override val nameString: String,
    val crosshairType: Type,
    val crosshair: Crosshair
) : Nameable {

    // Normal
    Cross("Cross", Type.NORMAL, CrosshairCross),

    // Gun
    BanditEvo("Bandit Evo", Type.GUN, CrosshairBanditEvo),
    BR75("BR75", Type.GUN, CrosshairBR75),
    MA40("MA40 AR", Type.GUN, CrosshairMA40);

    enum class Type {
        GUN,
        NORMAL
    }

}
