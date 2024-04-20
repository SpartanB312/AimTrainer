package net.spartanb312.everett.utils.misc

import net.spartanb312.everett.utils.timing.Duration
import net.spartanb312.everett.utils.timing.Timer

class Limiter(
    var limit: Int,
    var timeUnit: Duration = Duration.Millisecond,
    val block: () -> Unit
) {
    val updateTimer = Timer()
    fun invoke() = updateTimer.passedAndReset(limit, timeUnit, block = block)
}