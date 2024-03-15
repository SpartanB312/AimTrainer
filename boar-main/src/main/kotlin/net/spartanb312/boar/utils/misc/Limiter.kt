package net.spartanb312.boar.utils.misc

import net.spartanb312.boar.utils.timing.Duration
import net.spartanb312.boar.utils.timing.Timer

class Limiter(
    var limit: Int,
    var timeUnit: Duration = Duration.Millisecond,
    val block: () -> Unit
) {
    val updateTimer = Timer()
    fun invoke() = updateTimer.passedAndReset(limit, timeUnit, block = block)
}