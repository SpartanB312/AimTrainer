package net.spartanb312.boar.utils.misc

import net.spartanb312.boar.utils.collection.CircularArray

/**
 * Created on 2/11/2023 by B312
 */
class ActualCounter(val interval: Int) {

    private val cache = CircularArray<Int>(100)
    private var lastInvokeTime = System.currentTimeMillis()
    private var lastStatisticTime = System.currentTimeMillis()

    var cps = 0.0; private set

    fun invoke() {
        val currentTime = System.currentTimeMillis()

        val intervalTime = currentTime - lastInvokeTime
        lastInvokeTime = currentTime
        cache.add(intervalTime)

        if (currentTime - lastStatisticTime >= interval) {
            val intervalList = cache.toList()
            if (intervalList.isNotEmpty()) {
                cps = 1000.0 / intervalList.average()
            }
        }
    }

}