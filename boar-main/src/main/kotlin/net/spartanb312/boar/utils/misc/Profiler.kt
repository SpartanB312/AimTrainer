package net.spartanb312.boar.utils.misc

class Profiler {

    private val durations = Array(100) { 0L }
    private val names = Array(100) { "" }
    private var currentIndex = 0
    private var startTime = 0L

    fun start() {
        startTime = System.nanoTime()
        currentIndex = 0
    }

    fun profiler(name: String) {
        durations[currentIndex] = System.nanoTime()
        names[currentIndex] = name
        currentIndex++
    }

    fun endWithResult(): Map<String, Long> {
        currentIndex = 0
        val map = mutableMapOf<String, Long>()
        for (i in 0 until currentIndex) {
            map[names[i]] = durations[i]
        }
        return map
    }

}