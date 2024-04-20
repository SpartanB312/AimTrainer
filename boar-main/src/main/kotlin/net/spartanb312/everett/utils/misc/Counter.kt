package net.spartanb312.everett.utils.misc

import net.spartanb312.everett.utils.timing.FastTimer
import net.spartanb312.everett.utils.timing.passedAndReset

/**
 * Created on 2/12/2023 by B312
 */
class Counter(val interval: Int) {
    private var count = 0
    var cps = 0; private set

    private var timer = FastTimer()

    fun invoke(action: ((Int) -> Unit)? = null) {
        timer = timer.passedAndReset(interval, {
            cps = count
            count = 0
            action?.invoke(cps)
        }, { count++ })
    }
}