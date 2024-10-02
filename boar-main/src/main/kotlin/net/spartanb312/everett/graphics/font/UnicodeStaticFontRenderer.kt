package net.spartanb312.everett.graphics.font

import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.OpenGL.*
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.RenderSystem
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer.draw
import net.spartanb312.everett.graphics.matrix.scalef
import net.spartanb312.everett.graphics.matrix.scope
import net.spartanb312.everett.graphics.matrix.translatef
import net.spartanb312.everett.graphics.texture.MipmapTexture
import net.spartanb312.everett.graphics.texture.Texture
import net.spartanb312.everett.graphics.texture.loader.LazyTextureContainer
import net.spartanb312.everett.graphics.texture.loader.TextureLoader
import net.spartanb312.everett.graphics.texture.useTexture
import net.spartanb312.everett.utils.Logger
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.encryption.impl.AES
import net.spartanb312.everett.utils.math.ceilToInt
import net.spartanb312.everett.utils.misc.createFile
import net.spartanb312.everett.utils.misc.limitLength
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class UnicodeStaticFontRenderer(
    val text: String,
    private val font: Font,
    private val antiAlias: Boolean = true,
    private val fractionalMetrics: Boolean = false,
    private val imgWidth: Int = 1024,
    private val imgHeight: Int = 64,
    private val linearMag: Boolean = true,
    private val useMipmap: Boolean = true,
    private val qualityLevel: Int = 3,
    private val offsetPixel: Int = 0,
    override var scaleFactor: Float = 1f,
    private val textureLoader: TextureLoader? = null,
    override val asyncLoad: () -> Boolean = { true }
) : StaticFontRenderer {

    private val shadowColor = ColorRGB.BLACK.alpha(128)
    private var imgSupplier: (() -> BufferedImage)? = null
    override var absoluteWidth = 0
    override var absoluteHeight = 0
    override val isReady get() = texture.available

    private val useAsyncLoader get() = textureLoader != null && asyncLoad.invoke()

    fun setScale(scale: Float): UnicodeStaticFontRenderer {
        this.scaleFactor = scale
        return this
    }

    private fun initTexture(instantLoad: Boolean = false): Texture {
        Logger.debug("Init static texture")
        val imgSupplier = imgSupplier
        if (imgSupplier != null) {
            val texture: Texture = if (!instantLoad && useAsyncLoader) {
                val texture = LazyTextureContainer(
                    MipmapTexture.lateUpload(GL_RGBA, 3, useMipmap, qualityLevel),
                    imgSupplier
                ).useTexture {
                    if (!linearMag) glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
                }
                textureLoader!!.add(texture)
                texture
            } else {
                val img = imgSupplier.invoke()
                MipmapTexture(img, GL_RGBA, 3, useMipmap, qualityLevel).useTexture {
                    if (!linearMag) glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
                }
            }
            texture0 = texture
            return texture
        }
        val asyncJob: () -> BufferedImage = {
            val img = BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB)
            (img.createGraphics()).let {
                it.font = font
                it.color = Color(255, 255, 255, 0)
                it.fillRect(0, 0, imgWidth, imgHeight)
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
                var posY = 0
                for (char in text) {
                    val dimension = metrics.getStringBounds(char.toString(), it)
                    val width = dimension.bounds.width
                    val height = dimension.bounds.height
                    val offsetWidth = width + offsetPixel //+ scaledOffset * 2 // Originally 2
                    if (height > charHeight) {
                        charHeight = height
                        if (charHeight > this.absoluteHeight) this.absoluteHeight =
                            charHeight // Set the max height as Font height
                    }
                    if (posX + offsetWidth > imgWidth) {
                        posX = 0
                        posY += charHeight
                        charHeight = 0
                    }

                    it.drawString(
                        char.toString(),
                        posX + offsetPixel,
                        posY + metrics.ascent
                    )
                    posX += offsetWidth
                    absoluteWidth += offsetWidth
                }
            }
            image0 = img
            img
        }
        val texture: Texture = if (!instantLoad && useAsyncLoader) {
            val texture = LazyTextureContainer(
                MipmapTexture.lateUpload(GL_RGBA, 3, useMipmap, qualityLevel),
                asyncJob
            ).useTexture {
                if (!linearMag) glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            }
            textureLoader!!.add(texture)
            texture
        } else {
            val img = asyncJob.invoke()
            MipmapTexture(img, GL_RGBA, 3, useMipmap, qualityLevel).useTexture {
                if (!linearMag) glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            }
        }
        texture0 = texture
        return texture
    }

    private var texture0: Texture? = null
    private var image0: BufferedImage? = null
    private val texture: Texture
        get() = this.texture0 ?: initTexture()

    private val image: BufferedImage
        get() = this.image0 ?: initTexture(true).let {
            image0!!
        }

    override fun getWidth(scale: Float): Float = absoluteWidth * scale * this.scaleFactor
    override fun getHeight(scale: Float): Float = absoluteHeight * scale * this.scaleFactor

    override fun _drawString(
        x: Float,
        y: Float,
        color1: ColorRGB,//RT
        color2: ColorRGB,//LT
        color3: ColorRGB,//LB
        color4: ColorRGB,//RB
        scale0: Float,
        shadow: Boolean
    ) {
        var startX = x
        var startY = y
        val scale = scale0 * this.scaleFactor
        RS.matrixLayer.scope {
            if (scale != 1f) {
                translatef(x, y, 0f)
                scalef(scale, scale, 1f)
                startX = 0f
                startY = 0f
            }
            val endX = startX + imgWidth
            val endY = startY + imgHeight
            val alpha = ((color1.a + color2.a + color3.a + color4.a) / 4f).ceilToInt().coerceAtMost(255)

            texture.useTexture {
                GLHelper.blend = true
                GL_TRIANGLE_STRIP.draw(PersistentMappedVertexBuffer.VertexMode.Universal) {
                    universal(endX, startY, 1f, 0f, if (shadow) shadowColor.alpha(alpha) else color1)
                    universal(startX, startY, 0f, 0f, if (shadow) shadowColor.alpha(alpha) else color2)
                    universal(endX, endY, 1f, 1f, if (shadow) shadowColor.alpha(alpha) else color4)
                    universal(startX, endY, 0f, 1f, if (shadow) shadowColor.alpha(alpha) else color3)
                }
            }
        }
    }

    /**
     * Experimental function
     * FontRenderer cache
     */
    override val serialCode = AES.encode(
        text + font.fontName + font.size + antiAlias + fractionalMetrics + imgWidth + imgHeight + linearMag + useMipmap + qualityLevel + scaleFactor,
        "spartan9292"
    ).replace("/", "s").replace("=", "e").replace("+", "p").limitLength(64)

    override fun saveCache(savePath: String): Boolean {
        // Create img file, invoke image to update
        if (image0 == null && !RenderSystem.isRenderThread()) return false
        try {
            createFile(savePath + "img.png").let {
                ImageIO.write(image, "png", it)
            }
        } catch (exception: Exception) {
            Logger.error("Failed to create image file!")
            exception.printStackTrace()
            return false
        }

        // Create manifest file
        try {
            createFile(savePath + "MANIFEST").apply {
                writeText(
                    listOf(
                        "#SerialCode=$serialCode",
                        "width=$absoluteWidth",
                        "height=$absoluteHeight"
                    ).joinToString("\n")
                )
            }
        } catch (exception: Exception) {
            Logger.error("Failed to create manifest file!")
            exception.printStackTrace()
            return false
        }
        return true
    }

    override fun readCache(readPath: String): Boolean {
        val manifest = File(readPath + "MANIFEST")
        if (!manifest.exists()) return false
        manifest.readLines().forEach {
            if (it.startsWith("#SerialCode=")) {
                if (it.substringAfter("#SerialCode=") != serialCode) return false
            } else {
                if (it.startsWith("width=")) absoluteWidth = it.substringAfter("width=").toInt()
                if (it.startsWith("height=")) absoluteHeight = it.substringAfter("height=").toInt()
            }
        }
        val imgFile = File(readPath + "img.png")
        if (!imgFile.exists()) return false
        imgSupplier = {
            val img = ImageIO.read(imgFile.inputStream())
            image0 = img
            img
        }
        return true
    }

}