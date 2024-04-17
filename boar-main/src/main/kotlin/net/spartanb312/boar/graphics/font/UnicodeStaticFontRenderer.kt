package net.spartanb312.boar.graphics.font

import net.spartanb312.boar.graphics.GLHelper
import net.spartanb312.boar.graphics.OpenGL.*
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.RenderSystem
import net.spartanb312.boar.graphics.drawing.VertexFormat
import net.spartanb312.boar.graphics.drawing.buffer.ArrayedVertexBuffer.buffer
import net.spartanb312.boar.graphics.drawing.buffer.PersistentMappedVertexBuffer
import net.spartanb312.boar.graphics.drawing.buffer.PersistentMappedVertexBuffer.draw
import net.spartanb312.boar.graphics.texture.MipmapTexture
import net.spartanb312.boar.graphics.texture.Texture
import net.spartanb312.boar.graphics.texture.loader.LazyTextureContainer
import net.spartanb312.boar.graphics.texture.loader.TextureLoader
import net.spartanb312.boar.graphics.texture.useTexture
import net.spartanb312.boar.utils.Logger
import net.spartanb312.boar.utils.color.ColorRGB
import net.spartanb312.boar.utils.encryption.impl.AES
import net.spartanb312.boar.utils.math.ceilToInt
import net.spartanb312.boar.utils.misc.createFile
import net.spartanb312.boar.utils.misc.limitLength
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
    private val offsetPixel: Int = 0,
    override var scaleFactor: Float = 1f,
    private val textureLoader: TextureLoader? = null
) : StaticFontRenderer {

    private val shadowColor = ColorRGB.BLACK.alpha(128)
    private var imgSupplier: (() -> BufferedImage)? = null
    override var absoluteWidth = 0
    override var absoluteHeight = 0
    override val isReady get() = texture.available

    fun setScale(scale: Float): UnicodeStaticFontRenderer {
        this.scaleFactor = scale
        return this
    }

    private fun initTexture(instantLoad: Boolean = false): Texture {
        Logger.debug("Init static texture")
        val imgSupplier = imgSupplier
        if (imgSupplier != null) {
            val texture: Texture = if (!instantLoad && textureLoader != null) {
                val texture = LazyTextureContainer(
                    MipmapTexture.lateUpload(GL_RGBA, useMipmap = useMipmap),
                    imgSupplier
                ).useTexture {
                    if (!linearMag) glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
                }
                textureLoader.add(texture)
                texture
            } else {
                val img = imgSupplier.invoke()
                MipmapTexture(img, GL_RGBA, useMipmap = useMipmap).useTexture {
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
        if (scale != 1f) {
            GLHelper.pushMatrix(GL_MODELVIEW)
            glTranslatef(x, y, 0f)
            glScalef(scale, scale, 1f)
            startX = 0f
            startY = 0f
        }
        val endX = startX + imgWidth
        val endY = startY + imgHeight
        val alpha = ((color1.a + color2.a + color3.a + color4.a) / 4f).ceilToInt().coerceAtMost(255)

        texture.useTexture {
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
            GLHelper.alpha = true
            GLHelper.blend = true
            if (RS.compatMode) {
                GLHelper.texture2d = true
                GL_QUADS.buffer(VertexFormat.Pos2fColorTex, 4) {
                    v2Tex2fC(endX, startY, 1f, 0f, if (shadow) shadowColor.alpha(alpha) else color1)
                    v2Tex2fC(startX, startY, 0f, 0f, if (shadow) shadowColor.alpha(alpha) else color2)
                    v2Tex2fC(startX, endY, 0f, 1f, if (shadow) shadowColor.alpha(alpha) else color3)
                    v2Tex2fC(endX, endY, 1f, 1f, if (shadow) shadowColor.alpha(alpha) else color4)
                }
                GLHelper.texture2d = false
            } else {
                GL_QUADS.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
                    universe(endX, startY, 1f, 0f, if (shadow) shadowColor.alpha(alpha) else color1)
                    universe(startX, startY, 0f, 0f, if (shadow) shadowColor.alpha(alpha) else color2)
                    universe(startX, endY, 0f, 1f, if (shadow) shadowColor.alpha(alpha) else color3)
                    universe(endX, endY, 1f, 1f, if (shadow) shadowColor.alpha(alpha) else color4)
                }
            }
        }

        if (scale != 1f) {
            GLHelper.popMatrix(GL_MODELVIEW)
        }
    }

    /**
     * Experimental function
     * FontRenderer cache
     */
    override val serialCode = AES.encode(
        text + font.fontName + font.size + antiAlias + fractionalMetrics + imgWidth + imgHeight + linearMag + useMipmap + scaleFactor,
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