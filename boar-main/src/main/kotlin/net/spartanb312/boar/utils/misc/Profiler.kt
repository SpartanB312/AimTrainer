package net.spartanb312.boar.utils.misc

class Profiler {

    private val durations = Array(100) { 0L }
    private val names = Array(100) { "" }
    private var currentIndex = 0
    private var startTime = 0L

    fun start(): Profiler {
        startTime = System.nanoTime()
        currentIndex = 0
        return this
    }

    fun profiler(name: String): Profiler {
        val currentTime = System.nanoTime()
        durations[currentIndex] = currentTime - startTime
        names[currentIndex] = name
        startTime = currentTime
        currentIndex++
        return this
    }

    fun endWithResult(): MutableMap<String, Long> {
        val map = mutableMapOf<String, Long>()
        for (i in 0 until currentIndex) {
            map[names[i]] = durations[i]
        }
        currentIndex = 0
        return map
    }

}