package net.spartanb312.boar.graphics.texture.loader

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.spartanb312.boar.graphics.texture.AbstractTexture
import net.spartanb312.boar.graphics.texture.delegate.LateUploadTexture
import net.spartanb312.boar.utils.thread.ConcurrentTaskManager
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

class AsyncTextureLoader(private val threadsCount: Int) : TextureLoader {

    private val workingThread = AtomicInteger()
    private val ioThread = AtomicInteger()
    private val id = Companion.id.getAndIncrement()
    private val loadCount = AtomicInteger()
    override val totalThread get() = max(threadsCount, activeThread)
    override val activeThread get() = workingThread.get()
    override val loadedCount get() = loadCount.get()
    override val queue = LinkedBlockingQueue<LazyTextureContainer>()
    private val renderThreadJobs = LinkedBlockingQueue<() -> LateUploadTexture>()
    private val finishedTextures = mutableListOf<LateUploadTexture>()
    private val taskManager = ConcurrentTaskManager("AsyncTextureLoader" + if (id > 0) "-$id" else "", threadsCount)
    private val repeatTask = taskManager.runRepeat(1, true) {
        while (true) {
            val container = queue.poll()
            if (container != null) {
                taskManager.launch(Dispatchers.IO) {
                    workingThread.incrementAndGet()
                    ioThread.incrementAndGet()
                    if (container.state.get() == AbstractTexture.State.CREATED) {
                        val job = container.asyncLoad {
                            workingThread.decrementAndGet()
                            loadCount.incrementAndGet()
                        }
                        ioThread.decrementAndGet()
                        renderThreadJobs.put(job)
                    }
                }
            } else break
        }
    }

    override fun renderThreadHook(timeLimit: Int) {
        val startTime = System.currentTimeMillis()
        while (true) {
            val job = renderThreadJobs.poll()
            if (job != null) finishedTextures.add(job.invoke())
            else break
            if (timeLimit > 0 && System.currentTimeMillis() - startTime > timeLimit) break
        }
    }

    override fun add(texture: LazyTextureContainer) {
        if (taskManager.available()) {
            queue.add(texture)
        } else throw Exception("This texture loader has been shut down!")
    }

    override fun resume() = repeatTask.resume()

    override fun suspend() = repeatTask.suspend()

    override fun stop() = taskManager.shutdown()

    override fun remaining(): Int {
        return queue.size + ioThread.get()
    }

    companion object {
        private val id = AtomicInteger(0)
    }

}