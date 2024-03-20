package net.spartanb312.boar.graphics.font

import net.spartanb312.boar.graphics.GLHelper
import net.spartanb312.boar.graphics.OpenGL.*
import net.spartanb312.boar.graphics.drawing.VertexBuffer.buffer
import net.spartanb312.boar.graphics.drawing.VertexFormat
import net.spartanb312.boar.graphics.texture.MipmapTexture
import net.spartanb312.boar.graphics.texture.Texture
import net.spartanb312.boar.graphics.texture.loader.LazyTextureContainer
import net.spartanb312.boar.graphics.texture.loader.TextureLoader
import net.spartanb312.boar.graphics.texture.useTexture
import net.spartanb312.boar.utils.Logger
import net.spartanb312.boar.utils.color.ColorRGB
import net.spartanb312.boar.utils.encryption.impl.AES
import net.spartanb312.boar.utils.math.ceilToInt
import net.spartanb312.boar.utils.math.floorToInt
import net.spartanb312.boar.utils.misc.createFile
import net.spartanb312.boar.utils.misc.limitLength
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class UnicodeFontRenderer(
    private val font: Font,
    size: Int,
    private val antiAlias: Boolean = true,
    private val fractionalMetrics: Boolean = false,
    private val imgSize: Int = 512,
    private val chunkSize: Int = 64,
    private val linearMag: Boolean = true,
    private val useMipmap: Boolean = true,
    override var scaleFactor: Float = 1f,
    private val textureLoader: TextureLoader? = null
) : FontRenderer {

    private val shadowColor = ColorRGB.BLACK.alpha(128)
    private val charDataArray = arrayOfNulls<CharData>(65536)
    private val scaledOffset = (4 * size / 25f).toInt()
    override var absoluteHeight = 0

    private val chunkAmount = 65536 / chunkSize
    private val textures = arrayOfNulls<Texture>(chunkAmount)
    private val badChunks = Array(chunkAmount) { 0 }
    private val loadedChunk = arrayOfNulls<BufferedImage>(chunkAmount)
    override val loadedChunks = mutableListOf<Int>()
    private val chunkCaches = mutableMapOf<Int, ChunkCache>() // ID, Cache

    fun setScale(scale: Float): UnicodeFontRenderer {
        this.scaleFactor = scale
        return this
    }

    init {
        // Init ASCII chunks
        for (chunk in 0 until (256 / chunkSize)) {
            initChunk(chunk)
        }
    }

    override fun initChunkForce(chunk: Int, instantLoad: Boolean) {
        initChunk(chunk, instantLoad)
    }

    private fun initChunk(chunk: Int, instantLoad: Boolean = false): Texture {
        loadedChunks.add(chunk)
        if (loadedChunk[chunk] != null) {
            Logger.debug("Chunk $chunk already loaded")
            return textures[chunk]!!
        }

        val chunkImgSupplier = readChunkImgFromCache(chunk)
        if (chunkImgSupplier == null) {
            val asyncJob: () -> BufferedImage = {
                val img = BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_ARGB)
                (img.createGraphics()).let {
                    it.font = this.font
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
                    val metrics = it.fontMetrics
                    var charHeight = 0
                    var posX = 0
                    var posY = 1
                    for (index in 0 until chunkSize) {
                        val dimension = metrics.getStringBounds((chunk * chunkSize + index).toChar().toString(), it)
                        CharData(
                            dimension.bounds.width,
                            dimension.bounds.height
                        ).also { charData ->
                            val imgWidth = charData.width + scaledOffset * 2
                            if (charData.height > charHeight) {
                                charHeight = charData.height
                                if (charHeight > absoluteHeight) absoluteHeight =
                                    charHeight // Set the max height as Font height
                            }
                            if (posX + imgWidth > imgSize) {
                                posX = 0
                                posY += charHeight
                                charHeight = 0
                            }
                            charData.u = (posX + scaledOffset) / imgSize.toFloat()
                            charData.v = posY / imgSize.toFloat()
                            charData.u1 = (posX + scaledOffset + charData.width) / imgSize.toFloat()
                            charData.v1 = (posY + charData.height) / imgSize.toFloat()
                            charDataArray[chunk * chunkSize + index] = charData
                            it.drawString(
                                (chunk * chunkSize + index).toChar().toString(),
                                posX + scaledOffset,
                                posY + metrics.ascent
                            )
                            posX += imgWidth
                        }
                    }
                }
                loadedChunk[chunk] = img
                img
            }
            val texture: Texture = if (!instantLoad && textureLoader != null) {
                val texture = LazyTextureContainer(
                    MipmapTexture.lateUpload(GL_RGBA, useMipmap = useMipmap),
                    asyncJob
                ).useTexture {
                    if (!linearMag) glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
                }
                textureLoader.add(texture)
                texture
            } else {
                val img = asyncJob.invoke()
                MipmapTexture(img, GL_RGBA, useMipmap = useMipmap).useTexture {
                    if (!linearMag) glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
                }
            }
            textures[chunk] = texture
            return texture
        } else {
            Logger.debug("Init chunk $chunk from cache")
            val texture = if (!instantLoad && textureLoader != null) {
                val texture = LazyTextureContainer(
                    MipmapTexture.lateUpload(GL_RGBA, useMipmap = useMipmap)
                ) {
                    val img = chunkImgSupplier.invoke()
                    loadedChunk[chunk] = img
                    img
                }.useTexture {
                    if (!linearMag) glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
                }
                textureLoader.add(texture)
                texture
            } else {
                val img = chunkImgSupplier.invoke()
                loadedChunk[chunk] = img
                MipmapTexture(img, GL_RGBA, useMipmap = useMipmap).useTexture {
                    if (!linearMag) glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
                }
            }
            textures[chunk] = texture
            return texture
        }
    }

    inner class CharData(
        val width: Int,
        val height: Int,
    ) {
        var u = 0f
        var v = 0f
        var u1 = 0f
        var v1 = 1f
    }

    fun getWidth(char: Char, scale: Float = 1f): Float {
        return (charDataArray.getOrNull(char.code)?.width ?: 0) * scale * this.scaleFactor
    }

    override fun _getWidth(text: String): Int {
        var sum = 0
        var shouldSkip = false
        for (index in text.indices) {
            if (shouldSkip) {
                shouldSkip = false
                continue
            }
            val char = text[index]
            val chunk = char.code / chunkSize
            if (badChunks[chunk] == 1) continue
            if (textures.getOrNull(chunk) == null) {
                val newTexture = try {
                    initChunk(chunk)
                } catch (ignore: Exception) {
                    badChunks[chunk] = 1
                    null
                }
                textures[chunk] = newTexture
            }
            val delta = charDataArray.getOrNull(char.code)?.width ?: 0
            if (char == '§' || char == '&') {
                val next = text.getOrNull(index + 1)
                if (next != null && next in "0123456789abcdefr") {
                    shouldSkip = true
                }
                continue
            } else sum += delta
        }
        return sum
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
        shadow: Boolean,
    ) {
        GLHelper.smooth = true
        GLHelper.texture2d = true
        GLHelper.blend = true
        GLHelper.alpha = true
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        var startX = x
        var startY = y

        //Color
        val alpha = color0.a
        var currentColor = color0

        //Gradient
        val width = if (gradient) {
            assert(colors.size > 1)
            _getWidth(text).toFloat() / (colors.size - 1)
        } else 0f
        if (gradient && sliceMode && colors.size < text.length) throw Exception("Slice mode enabled. colors size should >= string length")
        val missingLastColor = colors.size == text.length

        //Scale
        val scale = scale0 * this.scaleFactor
        if (scale != 1f) {
            GLHelper.pushMatrix(GL_MODELVIEW)
            glTranslatef(x, y, 0f)
            glScalef(scale, scale, 1f)
            startX = 0f
            startY = 0f
        }
        var chunk = -1
        var shouldSkip = false
        for (index in text.indices) {
            if (shouldSkip) {
                shouldSkip = false
                continue
            }
            val char = text[index]
            if (char == '\n') {
                startY += absoluteHeight
                startX = x
                continue
            }
            if (char == '§' || char == '&') {
                val next = text.getOrNull(index + 1)
                if (next != null) {
                    //Color
                    val newColor = next.getColor(color0)
                    if (newColor != null) {
                        if (!gradient) currentColor = newColor.alpha(alpha)
                        shouldSkip = true
                        continue
                    }
                }
            }
            val currentChunk = char.code / chunkSize
            if (currentChunk != chunk) {
                chunk = currentChunk
                val texture = textures[chunk]
                if (texture == null) {
                    // If this is a bad chunk then we skip it
                    if (badChunks[chunk] == 1) continue
                    val newTexture = try {
                        initChunk(chunk)
                    } catch (ignore: Exception) {
                        badChunks[chunk] = 1
                        null
                    }
                    if (newTexture == null) {
                        continue
                    } else {
                        textures[chunk] = newTexture
                        newTexture.bindTexture()
                    }
                } else texture.bindTexture()
            }
            val data = charDataArray.getOrNull(char.code) ?: continue

            val endX = startX + data.width
            val endY = startY + data.height

            var leftColor = currentColor
            var rightColor = if (gradient) {
                if (sliceMode) {
                    if (missingLastColor && index == text.length - 1) colors[0]
                    else colors[index + 1]
                } else {
                    val ratio = ((endX - x) / width).coerceAtMost(colors.size - 1f).coerceAtLeast(0f)
                    colors[ratio.floorToInt()].mix(colors[ratio.ceilToInt()], ratio - ratio.floorToInt())
                }
            } else currentColor

            if (shadow) {
                leftColor = shadowColor.alpha(alpha)
                rightColor = shadowColor.alpha(alpha)
            }

            GL_QUADS.buffer(VertexFormat.Pos3fTex, 4) {
                //RT
                v3Tex2fC(
                    endX,
                    startY,
                    0f,
                    data.u1,
                    data.v,
                    rightColor
                )
                //LT
                v3Tex2fC(
                    startX,
                    startY,
                    0f,
                    data.u,
                    data.v,
                    leftColor
                )
                //LB
                v3Tex2fC(
                    startX,
                    endY,
                    0f,
                    data.u,
                    data.v1,
                    leftColor
                )
                //RB
                v3Tex2fC(
                    endX,
                    endY,
                    0f,
                    data.u1,
                    data.v1,
                    rightColor
                )

                startX = endX
                currentColor = rightColor
            }
        }
        glBindTexture(GL_TEXTURE_2D, 0)
        if (scale != 1f) {
            GLHelper.popMatrix(GL_MODELVIEW)
        }
        GLHelper.texture2d = false
    }

    private fun Char.getColor(prev: ColorRGB = ColorRGB.WHITE): ColorRGB? = when (this) {
        '0' -> ColorRGB(0, 0, 0)
        '1' -> ColorRGB(0, 0, 170)
        '2' -> ColorRGB(0, 170, 0)
        '3' -> ColorRGB(0, 170, 170)
        '4' -> ColorRGB(170, 0, 0)
        '5' -> ColorRGB(170, 0, 170)
        '6' -> ColorRGB(255, 170, 0)
        '7' -> ColorRGB(170, 170, 170)
        '8' -> ColorRGB(85, 85, 85)
        '9' -> ColorRGB(85, 85, 255)
        'a' -> ColorRGB(85, 255, 85)
        'b' -> ColorRGB(85, 255, 255)
        'c' -> ColorRGB(255, 85, 85)
        'd' -> ColorRGB(255, 85, 255)
        'e' -> ColorRGB(255, 255, 85)
        'f' -> ColorRGB(255, 255, 255)
        'r' -> prev
        else -> null
    }

    override fun initAllChunks(instantLoad: Boolean) {
        for (chunk in 0 until chunkAmount) {
            initChunk(chunk, instantLoad)
        }
    }

    /**
     * Experimental function
     * FontRenderer cache
     */
    override val serialCode = AES.encode(
        font.fontName + font.size + antiAlias + fractionalMetrics + imgSize + chunkSize + linearMag + useMipmap + scaleFactor,
        "spartan1186"
    ).replace("/", "s").replace("=", "e").replace("+", "p").limitLength(64)

    override fun saveCache(savePath: String): Boolean = saveCache(savePath, false)

    override fun saveCache(savePath: String, initAllChunks: Boolean): Boolean {
        // Read previous chunk manifest
        val previousSavedChunks = mutableMapOf<Int, String>()
        val previousManifest = File(savePath + "MANIFEST")
        if (previousManifest.exists()) {
            for (line in previousManifest.readLines()) {
                if (line.startsWith("#SerialCode=")) {
                    if (line.substringAfter("#SerialCode=") != serialCode) {
                        Logger.debug("Serial Code doesn't match!")
                        break
                    }
                    continue
                }
                val array = line.split("=")
                previousSavedChunks[array[0].toInt()] = array[1]
            }
        }

        // Init all chunks
        if (initAllChunks) for (id in 0 until chunkAmount) {
            var cacheContainsNewChunk = false
            val previousSavedChunkName = previousSavedChunks[id]
            if (previousSavedChunkName != null) {
                // Check file exist
                val imgFile = File(savePath + "chunks/" + previousSavedChunkName + "/chunkImg.png")
                val charDataFile = File(savePath + "chunks/" + previousSavedChunkName + "/char.data")
                if (imgFile.exists() && charDataFile.exists()) {
                    cacheContainsNewChunk = true
                    Logger.debug("Cache already have chunk $id, no need to init")
                } else {
                    Logger.debug("Chunk[id=$id, name=$previousSavedChunkName] cache file is broken [imgFile=${imgFile.exists()}, charDataFile=${charDataFile.exists()}]")
                }
            }
            if (loadedChunk[id] == null && !cacheContainsNewChunk) {
                Logger.debug("Initializing chunk $id")
                initChunk(id, true)
            }
        }

        // Generate chunk file name
        val chunkIDtoNameMap = mutableMapOf<Int, String>()
        for (id in 0 until chunkAmount) {
            if (loadedChunk[id] != null) {
                var chunkName = AES.encode(serialCode.substring(0, 32) + id, "everett")
                    .replace("/", "s")
                    .replace("=", "e")
                    .replace("+", "p")
                chunkName = chunkName.substring(chunkName.length - 22, chunkName.length)
                chunkIDtoNameMap[id] = chunkName
                //Logger.info("ID:$id Name:$chunkName")
            }
        }

        // Generate chunk data
        val chunkDataMap = mutableMapOf<Int, ChunkData>() // ID, ChunkData
        try {
            for (id in 0 until chunkAmount) {
                val chunkImg = loadedChunk[id]
                if (chunkImg != null) {
                    val charDataRange = (id * chunkSize) until (id * chunkSize + chunkSize)
                    val charDataMap = mutableMapOf<Int, CharData>()
                    for (charIndex in charDataRange) {
                        charDataMap[charIndex] = charDataArray[charIndex]!!
                    }
                    chunkDataMap[id] = ChunkData(id, chunkImg, charDataMap)
                }
            }
        } catch (exception: Exception) {
            Logger.error("Failed to generate chunk data!")
            exception.printStackTrace()
            return false
        }

        // Save chunk data
        try {
            chunkDataMap.forEach { (chunkID, chunkData) ->
                var shouldCreate = true
                // check previous
                val previousName = previousSavedChunks[chunkID]
                if (previousName != null && previousName == chunkIDtoNameMap[chunkID]) {
                    shouldCreate = false
                    Logger.debug("Chunk[id=$chunkID, name=${chunkIDtoNameMap[chunkID]}] Previous cache exist and serial code match!")
                }
                if (shouldCreate) {
                    createFile(savePath + "chunks/" + chunkIDtoNameMap[chunkID] + "/chunkImg.png").let {
                        ImageIO.write(chunkData.img, "png", it)
                    }
                    createFile(savePath + "chunks/" + chunkIDtoNameMap[chunkID] + "/char.data").let {
                        val charDataList = mutableListOf<String>()
                        chunkData.charData.forEach { (charIndex, charData) ->
                            charDataList.add(
                                "$charIndex:${charData.u},${charData.u1},${charData.v},${charData.v1},${charData.width},${charData.height}"
                            )
                        }
                        it.writeText(charDataList.joinToString(separator = "\n"))
                    }
                }
            }
        } catch (exception: Exception) {
            Logger.error("Failed to save chunk data!")
            exception.printStackTrace()
            return false
        }

        // Create manifest file
        try {
            createFile(savePath + "MANIFEST").apply {
                val manifest = mutableListOf("#SerialCode=$serialCode")
                for (id in 0 until chunkAmount) {
                    val current = chunkIDtoNameMap[id]
                    if (current != null) {
                        manifest.add("$id=${chunkIDtoNameMap[id]}")
                        continue
                    }
                    val previous = previousSavedChunks[id]
                    if (previous != null) {
                        // Check exist
                        val previousSavedChunkName = previousSavedChunks[id]
                        val imgFile = File(savePath + "chunks/" + previousSavedChunkName + "/chunkImg.png")
                        val charDataFile = File(savePath + "chunks/" + previousSavedChunkName + "/char.data")
                        if (imgFile.exists() && charDataFile.exists()) {
                            manifest.add("$id=$previousSavedChunkName")
                        }
                    }
                }
                writeText(manifest.joinToString(separator = "\n"))
            }
        } catch (exception: Exception) {
            Logger.error("Failed to create manifest file!")
            exception.printStackTrace()
            return false
        }

        return true
    }

    override fun readCache(readPath: String): Boolean {
        // Read manifest file
        val manifestFile = File(readPath + "MANIFEST")
        if (!manifestFile.exists()) return false
        for (line in manifestFile.readLines()) {
            if (line.startsWith("#SerialCode=")) {
                if (line.substringAfter("#SerialCode=") != serialCode) return false
            } else {
                val array = line.split("=")
                val chunkID = array[0].toInt()
                val chunkName = array[1]
                val imgFile = File(readPath + "chunks/" + chunkName + "/chunkImg.png")
                val charDataFile = File(readPath + "chunks/" + chunkName + "/char.data")
                if (!imgFile.exists() || !charDataFile.exists()) {
                    Logger.debug("Chunk[id=$chunkID, name=$chunkName] files broken")
                    continue
                }
                val imgSupplier = { ImageIO.read(imgFile.inputStream()) }
                val charDataSupplier = {
                    val charDataMap = mutableMapOf<Int, CharData>()
                    charDataFile.readLines().forEach {
                        val charIndex = it.substringBefore(":").toInt()
                        val data = it.substringAfter(":").split(",")
                        val charData = CharData(data[4].toInt(), data[5].toInt()).apply {
                            u = data[0].toFloat()
                            u1 = data[1].toFloat()
                            v = data[2].toFloat()
                            v1 = data[3].toFloat()
                        }
                        charDataMap[charIndex] = charData
                    }
                    charDataMap
                }
                chunkCaches[chunkID] = ChunkCache(chunkID, imgSupplier, charDataSupplier)
            }
        }
        return true
    }

    private fun readChunkImgFromCache(chunk: Int): (() -> BufferedImage)? {
        val chunkCache = chunkCaches[chunk] ?: return null
        chunkCache.charDataSupplier.invoke().forEach { (charIndex, charData) ->
            charDataArray[charIndex] = charData
        }
        return chunkCache.imgSupplier
    }

    // For read
    private class ChunkCache(
        val chunkID: Int,
        val imgSupplier: () -> BufferedImage,
        val charDataSupplier: () -> Map<Int, CharData>
    )

    // For save
    private class ChunkData(
        val chunkID: Int,
        val img: BufferedImage,
        val charData: Map<Int, CharData>
    )

}