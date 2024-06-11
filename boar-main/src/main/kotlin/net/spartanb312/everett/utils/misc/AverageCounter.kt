package net.spartanb312.everett.utils.misc

import net.spartanb312.everett.utils.collection.CircularArray
import net.spartanb312.everett.utils.timing.Duration
import net.spartanb312.everett.utils.timing.Timer

class AverageCounter(private val interval: Int, private val slice: Int) {

    private val counter = Counter(interval)
    private val countArray = CircularArray<Float>(slice)
    private val countTimer = Timer()
    var rawCPS = 0; private set
    var averageCPS = 0F; private set

    fun invoke(action: ((Int) -> Unit)? = null) {
        counter.invoke {
            rawCPS = it
            averageCPS = countArray.toList().sum() / 8f
            action?.invoke(it)
        }
        val nanoTime = (interval * 1000000.0) / slice.toDouble()
        countTimer.passedAndReset(nanoTime.toInt(), Duration.Nanosecond) { countArray.add(rawCPS) }
    }

}