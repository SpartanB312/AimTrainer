package net.spartanb312.everett.physics

import net.spartanb312.everett.utils.Logger

object PhysicsSystem {

    private var delayNanoTime = 0L
    private var lastUpdateNanoTime = System.nanoTime()
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
        setTPS(tps)
        if (dedicateThread) changeThread(PhysicsThread())
    }

    fun setTPS(tps: Int) {
        delayNanoTime = (1000000000.0 / tps).toLong()
    }

    fun update() {
        if (!mainThread.isAlive) updateThread.interrupt()
        if (Thread.currentThread() == updateThread) { // Make sure on update thread
            val currentNanoTime = System.nanoTime()
            if (currentNanoTime - lastUpdateNanoTime >= delayNanoTime) {
                lastUpdateNanoTime = currentNanoTime
                controller?.update()
                // obj Updates
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