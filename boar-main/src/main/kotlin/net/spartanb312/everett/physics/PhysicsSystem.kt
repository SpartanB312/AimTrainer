package net.spartanb312.everett.physics

import net.spartanb312.everett.utils.Logger
import net.spartanb312.everett.utils.misc.AverageCounter
import net.spartanb312.everett.utils.timing.Timer

object PhysicsSystem {

    private var tps = 120
    private val tpsLimiter = Timer()
    private var controller: Controller? = null
    private var updateThread = Thread.currentThread()
    private var mainThread = Thread.currentThread()

    fun launch(
        controller: Controller,
        tps: Int,
        dedicateThread: Boolean = true,
        mainThread: Thread = Thread.currentThread()
    ) {
        this.controller = controller
        this.mainThread = mainThread
        this.tps = tps
        if (dedicateThread) changeThread(PhysicsThread())
    }

    fun setTPS(tps: Int) {
        this.tps = tps
    }

    private val tpsCounter = AverageCounter(1000, 8)
    val averageTPS get() = tpsCounter.averageCPS

    fun update() {
        if (!mainThread.isAlive) updateThread.interrupt()
        if (Thread.currentThread() == updateThread) { // Make sure on update thread
             tpsLimiter.tps(tps){
                tpsCounter.invoke()
                // obj Updates
                controller?.update()
            }
        } else {
            Logger.info("Physics system should be handled by physics thread")
            throw Exception("Physics system handled by non-PhysicsThread")
        }
    }

    class PhysicsThread : Thread("PhysicsThread") {
        override fun run() {
            while (!isInterrupted) {
                update()
            }
        }
    }

    fun isOnThread(thread: Thread): Boolean = updateThread == thread
    val isOnDedicate get() = updateThread is PhysicsThread

    fun changeThread(thread: Thread) {
        if (updateThread != thread) {
            if (updateThread is PhysicsThread) updateThread.interrupt()
            updateThread = thread
            if (thread is PhysicsThread) thread.start()
        }
    }

}