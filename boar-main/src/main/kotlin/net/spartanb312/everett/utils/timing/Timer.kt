package net.spartanb312.everett.utils.timing

/**
 * Created on 2/12/2023 by B312
 */
class Timer(val timeUnit: Duration = Duration.Millisecond) {

    private var time = System.currentTimeMillis()

    fun passed(interval: Int, timeUnit: Duration = this.timeUnit): Boolean {
        return System.currentTimeMillis() - time * this.timeUnit.multiplier > interval * timeUnit.multiplier
    }

    fun reset() {
        time = System.currentTimeMillis()
    }

    fun passedAndReset(interval: Int, timeUnit: Duration = this.timeUnit): Boolean {
        val result = passed(interval, timeUnit)
        if (result) reset()
        return result
    }

    inline fun passedAndReset(interval: Int, timeUnit: Duration = this.timeUnit, block: () -> Unit): Boolean {
        val result = passed(interval, timeUnit)
        if (result) {
            reset()
            block()
        }
        return result
    }

    inline fun passedAndRun(
        interval: Int,
        timeUnit: Duration = this.timeUnit,
        reset: Boolean = false,
        block: () -> Unit
    ): Boolean {
        val result = passed(interval, timeUnit)
        if (result) {
            block()
            if (reset) reset()
        }
        return result
    }

}