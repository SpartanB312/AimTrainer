package net.spartanb312.boar.game.render.gui.impls.option

import net.spartanb312.boar.game.config.setting.AbstractSetting
import net.spartanb312.boar.game.input.interfaces.MouseClickListener
import net.spartanb312.boar.game.input.interfaces.MouseReleaseListener
import net.spartanb312.boar.utils.math.vector.Vec2i
import net.spartanb312.boar.utils.timing.Timer
import kotlin.math.max
import kotlin.math.min

abstract class AbstractSettingComponent<T>(val setting: AbstractSetting<T>) : MouseClickListener, MouseReleaseListener {

    var x = 0f
    var y = 0f
    var width = 0f
    var height = 0f

    protected var offsetRate = 0f
    private val offsetTimer = Timer()

    private var reachableArea: Pair<Vec2i, Vec2i> = Pair(
        Vec2i(Int.MIN_VALUE, Int.MIN_VALUE),
        Vec2i(Int.MAX_VALUE, Int.MAX_VALUE)
    )

    fun setReachableArea(min: Vec2i, max: Vec2i) {
        reachableArea = Pair(min, max)
    }

    fun restoreReachableArea() {
        reachableArea = Pair(
            Vec2i(Int.MIN_VALUE, Int.MIN_VALUE),
            Vec2i(Int.MAX_VALUE, Int.MAX_VALUE)
        )
    }

    fun isReachable(x: Int, y: Int): Boolean {
        val area = reachableArea
        val min = area.first
        val max = area.second
        return x >= min.x && x <= max.x && y >= min.y && y <= max.y
    }

    abstract fun onRender2D(mouseX: Double, mouseY: Double, scale: Float, alpha: Float)

    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean = false

    override fun onMouseReleased(mouseX: Int, mouseY: Int, button: Int) {}

    fun isHoovered(mouseX: Double, mouseY: Double): Boolean {
        return mouseX >= min(x, x + width) && mouseX <= max(x, x + width)
                && mouseY >= min(y, y + height) && mouseY <= max(y, y + height)
    }

    open fun reset() {}

}