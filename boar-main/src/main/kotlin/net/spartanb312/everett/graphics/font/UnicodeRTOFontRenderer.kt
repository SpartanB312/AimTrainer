package net.spartanb312.everett.graphics.font

import it.unimi.dsi.fastutil.longs.Long2LongArrayMap
import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.texture.loader.TextureLoader
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.math.floorToInt
import net.spartanb312.everett.utils.thread.ConcurrentTaskManager
import net.spartanb312.everett.utils.timing.Timer
import java.awt.Font
import java.util.concurrent.LinkedBlockingQueue

class UnicodeRTOFontRenderer(
    font: Font,
    size: Int,
    private val antiAlias: Boolean = true,
    private val fractionalMetrics: Boolean = false,
    imgSize: Int = 512,
    chunkSize: Int = 64,
    private val linearMag: Boolean = true,
    private val useMipmap: Boolean = false, // Avoid glitching
    private val qualityLevel: Int = 3,
    scaleFactor: Float = 1f,
    private val textureLoader: TextureLoader? = null,
) : RTOFontRenderer {

    companion object {
        val taskManager = ConcurrentTaskManager("RTO")
    }

    private val dynamicFontRenderer = UnicodeFontRenderer(
        font,
        size,
        antiAlias,
        fractionalMetrics,
        imgSize,
        chunkSize,
        linearMag,
        useMipmap,
        qualityLevel,
        scaleFactor,
        textureLoader
    )

    // Delegates
    override val serialCode get() = dynamicFontRenderer.serialCode
    override var absoluteHeight
        get() = dynamicFontRenderer.absoluteHeight
        set(value) {
            updateCacheHeight(value)
            dynamicFontRenderer.absoluteHeight = value
        }
    override var scaleFactor
        get() = dynamicFontRenderer.scaleFactor
        set(value) {
            updateCacheScale(value)
            dynamicFontRenderer.scaleFactor = value
        }
    override val loadedChunks get() = dynamicFontRenderer.loadedChunks
    override val linkedStaticFontRenderer = CustomFont(font, textureLoader)

    class StaticText(val renderer: Lazy<StaticFontRenderer>, val timer: Timer = Timer())

    private val cacheL2 = Long2LongLinkedOpenHashMap() // code to freq
    private val cacheL1 = Long2ObjectArrayMap<StaticText>()
    private fun clearCache() {
        cacheL1.clear()
        cacheL2.clear()
        maxFreq = 0L
    }

    private fun updateCacheScale(scaleFactor: Float) {
        cacheL1.forEach {
            it.value.renderer.value.scaleFactor = scaleFactor
        }
    }

    private fun updateCacheHeight(absoluteHeight: Int) {
        cacheL1.forEach {
            it.value.renderer.value.absoluteHeight = absoluteHeight
        }
    }

    override fun _getWidth(text: String): Int {
        return dynamicFontRenderer._getWidth(text)
    }

    private var maxFreq = 0L
    private val cacheQueue = LinkedBlockingQueue<Pair<Long, String>>()

    init {
        val timer = Timer()
        fun run() {
            inner@ while (true) {
                val task = cacheQueue.poll()
                if (task != null) solve(task)
                else break@inner
            }
            timer.passedAndReset(1000) {
                Long2LongArrayMap(cacheL2).forEach { (code, freq) ->
                    val newFreq = (freq - (RS.averageFPS * 0.3f).floorToInt()).coerceAtLeast(0)
                    if (newFreq == 0L) {
                        cacheL2.remove(code)
                        if (cacheL1.get(code)?.timer?.passed(20000 * RS.rtoTime) == true) {
                            cacheL1.remove(code)
                        }
                    } else cacheL2[code] = newFreq
                }
            }
        }
        taskManager.runRepeat(1) { run() }
    }

    private fun solve(pair: Pair<Long, String>) {
        val code = pair.first
        val text = pair.second
        if (text.contains('&') || text.contains('§')) return
        val freq = cacheL2.getOrPut(code) { 0L } + 1
        cacheL2.put(code, freq)
        if (maxFreq < freq) maxFreq = freq
        if (freq >= (RS.activeFrames).toInt()) {
            cacheL1.getOrPut(code) {
                val width = _getWidth(text)
                StaticText(
                    lazy {
                        linkedStaticFontRenderer.create(
                            text,
                            antiAlias,
                            fractionalMetrics,
                            (width * 1.2).toInt(),
                            (absoluteHeight * 1.2).toInt(),
                            linearMag,
                            useMipmap,
                            qualityLevel,
                            0,
                            scaleFactor,
                            textureLoader
                        )
                    }
                )
            }
        }
    }

    override fun _drawString(
        text: String,
        x: Float,
        y: Float,
        color0: ColorRGB,
        gradient: Boolean,
        colors: Array<ColorRGB>,
        sliceMode: Boolean,
        scale0: Float,
        shadow: Boolean
    ) {
        if (RS.rto && text.length in 2..31 && !gradient) {
            val code = text.hashCode().toLong() shl 32 // and xxx
            cacheQueue.put(code to text)
            val staticRenderer: StaticText? = cacheL1.get(code)
            if (staticRenderer != null && staticRenderer.renderer.value.isReady) {
                staticRenderer.timer.reset()
                staticRenderer.renderer.value._drawString(
                    x,
                    y,
                    color0,
                    color0,
                    color0,
                    color0,
                    scale0,
                    shadow
                )
                return
            }
        }
        dynamicFontRenderer._drawString(text, x, y, color0, gradient, colors, sliceMode, scale0, shadow)
    }

    override fun saveCache(savePath: String, initAllChunks: Boolean): Boolean {
        // TODO: Link cache
        return dynamicFontRenderer.saveCache(savePath, initAllChunks)
    }

    override fun saveCache(savePath: String): Boolean {
        // TODO: Link cache
        return dynamicFontRenderer.saveCache(savePath)
    }

    override fun readCache(readPath: String): Boolean {
        // TODO: Link cache
        return dynamicFontRenderer.readCache(readPath)
    }

    override fun initAllChunks(instantLoad: Boolean) {
        dynamicFontRenderer.initAllChunks()
    }

    override fun initChunkForce(chunk: Int, instantLoad: Boolean) {
        dynamicFontRenderer.initChunkForce(chunk, instantLoad)
    }

}