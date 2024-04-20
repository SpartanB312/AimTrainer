package net.spartanb312.everett.utils.timing

/**
 * Created on 2/12/2023 by B312
 */
@JvmInline
value class FastTimer(private val time: Long = System.currentTimeMillis()) {
    fun passed(interval: Int): Boolean {
        return System.currentTimeMillis() - time > interval
    }

    inline fun passedAndRun(interval: Int, block: () -> Unit): Boolean {
        return if (passed(interval)) {
            block()
            true
        } else false
    }
}

inline fun FastTimer.passedAndReset(interval: Int, block: () -> Unit): FastTimer {
    return if (passed(interval)) {
        block()
        FastTimer()
    } else this
}

inline fun FastTimer.passedAndReset(interval: Int, block: () -> Unit, block2: () -> Unit): FastTimer {
    return if (passed(interval)) {
        block()
        FastTimer()
    } else {
        block2()
        this
    }
}