package net.spartanb312.everett.graphics.font

import net.spartanb312.everett.AimTrainer
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer
import net.spartanb312.everett.graphics.font.metrics.DummyFontMetrics
import net.spartanb312.everett.graphics.matrix.scalef
import net.spartanb312.everett.graphics.matrix.scope
import net.spartanb312.everett.graphics.matrix.translatef
import net.spartanb312.everett.graphics.texture.ImageUtils.getRGBArray
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.color.colorCode
import net.spartanb312.everett.utils.color.getColor
import net.spartanb312.everett.utils.encryption.impl.AES
import net.spartanb312.everett.utils.math.ceilToInt
import net.spartanb312.everett.utils.math.floorToInt
import net.spartanb312.everett.utils.math.vector.Vec2i
import net.spartanb312.everett.utils.misc.limitLength
import org.lwjgl.opengl.ARBSparseTexture
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL45
import org.lwjgl.opengl.GL46.*
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

/**
 * Ported from Gyre
 */
class UnicodeSparseFontRenderer(
    override var scaleFactor: Float = 1f,
    val font: Font,
    val imgSize: Int = 8192,
    val defMaxCount: Int = 16,
    val textureSlice: Int = 128,
    val padding: Int = 1,
    val antiAlias: Boolean = true,
    val fractionalMetrics: Boolean = false,
    val useMipmap: Boolean = false,
    val italicAngleDegree: Float = 10f
) : FontRenderer {

    init {
        AimTrainer.sparseFontRenderers.add(this)
    }

    // Old data
    override val serialCode = AES.encode(
        font.fontName + font.size + antiAlias + fractionalMetrics + imgSize + textureSlice + padding + useMipmap + scaleFactor,
        "spartan1186"
    ).replace("/", "s").replace("=", "e").replace("+", "p").limitLength(64)
    override var absoluteHeight: Int
        get() = fontHeight
        set(value) {}
    override val asyncLoad: () -> Boolean = { false }
    override val loadedChunks: MutableSet<Int> = mutableSetOf()
    private val shadowColor = ColorRGB.BLACK.alpha(128)

    // New data
    private val initRequests = LinkedBlockingQueue<Char>()
    private val scaledPadding = (padding * font.size / 12.5f).toInt()

    private var commitedRegion = BooleanArray(textureSlice * textureSlice) { false }
    private var initRequested = BooleanArray(65536) { false }
    private var charDataArray = arrayOfNulls<CharData>(65536)
    private val fontMetrics: DummyFontMetrics = DummyFontMetrics(font, fractionalMetrics, antiAlias, italicAngleDegree)
    private val fontHeight = fontMetrics.metricsHeight
    private val charHeight = fontMetrics.charHeight
    private val charAscent = fontMetrics.charAscent

    private val charWidthData: Pair<IntArray, IntArray> = (IntArray(65536) { -1 } to IntArray(65536) { -1 }).also {
        repeat(65536) { code ->
            val width = fontMetrics.charWidth(code.toChar())
            it.first[code] = width
            it.second[code] = width + fontMetrics.italicAddon
        }
    }
    var texture = createTexture()
    private var pointerX = 0
    private var pointerY = 0
    private fun createTexture() = GL45.glCreateTextures(GL11.GL_TEXTURE_2D).also {
        GL45.glTextureParameteri(it, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT)
        GL45.glTextureParameteri(it, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT)
        GL45.glTextureParameteri(
            it, GL11.GL_TEXTURE_MIN_FILTER,
            if (useMipmap) GL11.GL_LINEAR_MIPMAP_LINEAR else GL11.GL_LINEAR
        )
        GL45.glTextureParameteri(it, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL45.glTextureParameteri(it, ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_INDEX_ARB, 0)
        GL45.glTextureParameteri(it, ARBSparseTexture.GL_TEXTURE_SPARSE_ARB, GL11.GL_TRUE)
        GL45.glTextureStorage2D(it, 1, GL11.GL_RGBA8, imgSize, imgSize)
        // TODO: implements mipmap
    }

    override fun reset() {
        initRequests.clear()
        loadedChunks.clear()
        initRequested = BooleanArray(65536) { false }
        charDataArray = arrayOfNulls(65536)
        commitedRegion = BooleanArray(textureSlice * textureSlice) { false }
        pointerX = 0
        pointerY = 0
        GL45.glDeleteTextures(texture)
        texture = createTexture()
    }

    private fun getCharData(char: Char): CharData? {
        val code = char.code
        val charData = charDataArray[code]
        if (charData != null) return charData
        if (initRequested[code]) return null
        // request for char initialize
        initRequested[code] = true
        initRequests.add(char)
        loadedChunks.add(code)
        return null
    }

    private fun makeSureCommited(minX: Int, minY: Int, maxX: Int, maxY: Int) {
        val side = imgSize / textureSlice
        for (xScan in minX..maxX) {
            for (yScan in minY..maxY) {
                val pos = xScan + yScan * textureSlice
                if (!commitedRegion[pos]) {
                    commitedRegion[pos] = true
                    ARBSparseTexture.glTexturePageCommitmentEXT(
                        texture,
                        0,
                        xScan * side,
                        yScan * side,
                        0,
                        side,
                        side,
                        1,
                        true
                    )
                }
            }
        }
    }

    fun processRequests(maxCount: Int) {
        if (initRequests.isEmpty()) return
        var count = 0
        val requestedChar = mutableListOf<Char>()
        while (count < maxCount) {
            val request = initRequests.poll() ?: break
            requestedChar.add(request)
            count++
        }
        val totalWidth = requestedChar.sumOf { getRenderWidth(it).toInt() + scaledPadding * 2 }
        // Create img
        val charOnImg = mutableMapOf<Char, Pair<Vec2i, Vec2i>>() // char, <LT, RB>
        val img = BufferedImage(totalWidth, fontHeight + scaledPadding * 2, BufferedImage.TYPE_INT_ARGB).also { img ->
            (img.createGraphics()).let {
                it.font = font
                it.color = Color(255, 255, 255, 0)
                it.fillRect(0, 0, imgSize, imgSize)
                it.color = Color.white
                it.setRenderingHint(
                    RenderingHints.KEY_FRACTIONALMETRICS,
                    if (fractionalMetrics) RenderingHints.VALUE_FRACTIONALMETRICS_ON
                    else RenderingHints.VALUE_FRACTIONALMETRICS_OFF
                )
                it.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    if (antiAlias) RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                    else RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
                )
                it.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    if (antiAlias) RenderingHints.VALUE_ANTIALIAS_ON
                    else RenderingHints.VALUE_ANTIALIAS_OFF
                )
                var posX = 0
                val posY = 0
                for (char in requestedChar) {
                    val renderWidth = getRenderWidth(char).toInt()
                    val startX = posX + scaledPadding
                    charOnImg[char] = Vec2i(startX, posY) to Vec2i(startX + renderWidth, posY + charHeight)
                    it.drawString(char.toString(), startX, posY + charAscent)
                    posX += (renderWidth + scaledPadding * 2)
                }
            }
        }
        // Mapping from img to texture
        val yStride = fontHeight // + scaledPadding * 2
        var checkPointer = pointerX
        val metrics = fontMetrics.metrics

        fun commitTexture(
            texStartX: Int,
            texEndX: Int,
            texStartY: Int,
            texEndY: Int,
            imgStartX: Int,
            imgEndX: Int,
            imgStartY: Int,
            imgEndY: Int,
        ) {
            val commitWidth = texEndX - texStartX
            val commitHeight = texEndY - texStartY
            val subImg = img.getSubimage(imgStartX, imgStartY, imgEndX - imgStartX, imgEndY - imgStartY)
            makeSureCommited(
                (textureSlice * texStartX / imgSize.toFloat()).floorToInt(),
                (textureSlice * texStartY / imgSize.toFloat()).floorToInt(),
                (textureSlice * texEndX / imgSize.toFloat()).floorToInt().coerceAtMost(textureSlice - 1),
                (textureSlice * texEndY / imgSize.toFloat()).floorToInt().coerceAtMost(textureSlice - 1),
            )
            GL11.glPixelStorei(GL_UNPACK_ROW_LENGTH, subImg.width)
            GL45.glTextureSubImage2D(
                texture,
                0,
                texStartX,
                texStartY,
                commitWidth,
                commitHeight,
                GL_BGRA,
                GL_UNSIGNED_BYTE,
                subImg.getRGBArray()
            )
            GL11.glPixelStorei(GL_UNPACK_ROW_LENGTH, 0)
        }

        var imgStartX = charOnImg.values.firstOrNull()?.first?.x ?: 0
        var imgStartY = charOnImg.values.firstOrNull()?.first?.y ?: 0
        var imgEndX = 0
        var imgEndY = 0
        var uPointer = -1
        charOnImg.forEach { (char, pos) ->
            val code = char.code
            val xStride = (pos.second.x - pos.first.x) + scaledPadding * 2
            // check boundary
            if (checkPointer + xStride > imgSize) {
                // commit this line if check pointer moved
                if (checkPointer - pointerX > 0) commitTexture(
                    pointerX,
                    checkPointer - scaledPadding * 2,
                    pointerY,
                    pointerY + yStride,
                    imgStartX,
                    imgEndX,
                    imgStartY,
                    imgEndY
                )
                // move start pointer to next line
                checkPointer = 0
                uPointer = -1
                pointerX = 0
                pointerY += yStride
                imgStartX = pos.first.x
                imgStartY = pos.first.y
            }
            // move image pointer
            imgEndX = pos.second.x
            imgEndY = pos.second.y
            if (uPointer == -1) uPointer = pos.first.x
            // mapping: img->texture
            val startX = pointerX + pos.first.x - uPointer
            val endX = pointerX + pos.second.x - uPointer
            val startY = pointerY + pos.first.y
            val endY = pointerY + pos.second.y
            // mapping: uv
            val u = startX / imgSize.toFloat()
            val v = startY / imgSize.toFloat()
            val u1 = endX / imgSize.toFloat()
            val v1 = endY / imgSize.toFloat()
            // create char data
            var charWidth = metrics.charWidth(char)
            val renderWidth = getRenderWidth(char).toInt()
            val isEmpty = charWidth == 0
            if (isEmpty) charWidth = charHeight
            val charData = CharData(charWidth, charHeight, if (isEmpty) 0 else renderWidth)
            charData.u = u
            charData.v = v
            charData.u1 = u1
            charData.v1 = v1
            charDataArray[code] = charData
            // move check pointer
            checkPointer += xStride
        }
        // set to current pointer and commit
        if (checkPointer - pointerX > 0) {
            commitTexture(
                pointerX,
                checkPointer - scaledPadding * 2,
                pointerY,
                pointerY + yStride,
                imgStartX,
                imgEndX,
                imgStartY,
                imgEndY
            )
            pointerX = checkPointer
        }
    }

    private fun getRenderWidth(char: Char, scale: Float = 1f): Float {
        val index = char.code
        val existed = charWidthData.second[index]
        if (existed != -1) return existed * scale
        else {
            val width = fontMetrics.charRenderWidth(char)
            charWidthData.second[index] = width
            return width * scale
        }
    }

    override fun _getWidth(text: String): Int {
        return rawWidth(text, 1f).toInt()
    }

    override fun _drawString(
        text: String,
        x: Float,
        y: Float,
        color: ColorRGB,
        gradient: Boolean,
        colors: Array<ColorRGB>,
        sliceMode: Boolean,
        scale: Float,
        shadow: Boolean
    ) {
        val appliedScale = scale * this.scaleFactor
        val alpha = color.a
        var currentColor = color
        val width = if (gradient) {
            assert(colors.size > 1)
            rawWidth(text, 1f) / (colors.size - 1)
        } else 0f
        if (gradient && sliceMode && colors.size < text.length) throw Exception("Slice mode enabled. colors size should >= string length")
        val missingLastColor = colors.size == text.length
        RS.matrixLayer.scope {
            translatef(x, y, 0f)
            scalef(appliedScale, appliedScale, 1f)
            var startX = 0f
            var startY = 0f
            var shouldSkip = false
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture)
            with(PersistentMappedVertexBuffer.VertexMode.Universal) {
                val offsets = mutableListOf<Int>()
                val counts = mutableListOf<Int>()
                for (index in text.indices) {
                    if (shouldSkip) {
                        shouldSkip = false
                        continue
                    }
                    val char = text[index]
                    if (char == '\n') {
                        startY += fontHeight
                        startX = 0f
                        continue
                    }
                    if (char == '§' || char == '&') {
                        val next = text.getOrNull(index + 1)
                        if (next != null) {
                            val newColor = next.getColor(color)
                            if (newColor != null) {
                                if (!gradient) currentColor = newColor.alpha(alpha)
                                shouldSkip = true
                                continue
                            }
                        }
                    }
                    val data = getCharData(char) ?: continue
                    val endX = startX + data.renderWidth
                    val endY = startY + data.height
                    val leftColor = if (shadow) shadowColor else currentColor
                    val rightColor = if (shadow) shadowColor else if (gradient) {
                        if (sliceMode) {
                            assert(colors.size >= text.length) { "Slice mode requires colors size >= string length" }
                            if (missingLastColor && index == text.length - 1) colors[0]
                            else colors[index + 1]
                        } else {
                            val ratio = (endX / width).coerceAtMost(colors.size - 1f).coerceAtLeast(0f)
                            colors[ratio.floorToInt()].mix(colors[ratio.ceilToInt()], ratio - ratio.floorToInt())
                        }
                    } else currentColor
                    currentColor = rightColor
                    universal(
                        endX,
                        startY,
                        0f,
                        data.u1,
                        data.v,
                        rightColor
                    )
                    universal(
                        startX,
                        startY,
                        0f,
                        data.u,
                        data.v,
                        leftColor
                    )
                    universal(
                        endX,
                        endY,
                        0f,
                        data.u1,
                        data.v1,
                        rightColor
                    )
                    universal(
                        startX,
                        endY,
                        0f,
                        data.u,
                        data.v1,
                        leftColor
                    )
                    multiDrawPoint(offsets, counts)
                    startX += data.width
                }
                multiDraw(GL11.GL_TRIANGLE_STRIP, offsets, counts, shader)
            }
        }
    }

    private fun getCharWidth(char: Char, scale: Float): Float {
        val index = char.code
        val existed = charWidthData.first[index]
        if (existed != -1) return existed * scale
        else {
            val width = fontMetrics.charWidth(char)
            charWidthData.first[index] = width
            return width * scale
        }
    }

    private fun rawWidth(text: String, scale: Float): Float {
        var sum = 0f
        var shouldSkip = false
        for (index in text.indices) {
            if (shouldSkip) {
                shouldSkip = false
                continue
            }
            val char = text[index]
            val delta = getCharWidth(char, scale)
            if (char == '§' || char == '&') {
                val next = text.getOrNull(index + 1)
                if (next != null && next.colorCode != null) {
                    shouldSkip = true
                }
                continue
            } else sum += delta
        }
        return sum // specified scale has already been applied in getWidth(char,scale)
    }

    override fun readCache(readPath: String): Boolean {
        // Not available
        return true
    }

    override fun saveCache(savePath: String, initAllChunks: Boolean): Boolean {
        // Not available
        return true
    }

    override fun saveCache(savePath: String): Boolean {
        // Not available
        return true
    }

    override fun initAllChunks(instantLoad: Boolean) {
        for (i in 0..65535) getCharData(i.toChar())
    }

    override fun initChunkForce(chunk: Int, instantLoad: Boolean) {
        getCharData(chunk.toChar())
    }

    class CharData(
        val width: Int,
        val height: Int,
        val renderWidth: Int
    ) {
        var u = 0f
        var v = 0f
        var u1 = 0f
        var v1 = 1f

        @Override
        override fun toString(): String {
            return "CharData(width=$width, height=$height, u=$u, v=$v, u1=$u1, v1=$v1)"
        }
    }

    companion object {
        fun renderThreadHook(list: List<UnicodeSparseFontRenderer>, timeLimit: Int) {
            val startTime = System.currentTimeMillis()
            val queue = LinkedList(list)
            while (true) {
                val job = queue.poll()
                if (job != null) job.processRequests(job.defMaxCount)
                else break
                if (timeLimit > 0 && System.currentTimeMillis() - startTime > timeLimit) break
            }
        }
    }

}