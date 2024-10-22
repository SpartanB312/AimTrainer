package net.spartanb312.everett.graphics.font

import net.spartanb312.everett.graphics.texture.loader.TextureLoader
import net.spartanb312.everett.utils.ResourceHelper
import java.awt.Font

class CustomFont(override val font: Font, override val textureLoader: TextureLoader? = null) : StaticFont {

    companion object {
        @JvmStatic
        fun fromPath(
            path: String,
            size: Float,
            type: Int = Font.TRUETYPE_FONT,
            style: Int = Font.PLAIN,
            textureLoader: TextureLoader? = null
        ): CustomFont = CustomFont(
            Font.createFont(type, ResourceHelper.getResourceStream(path)!!)!!
                .deriveFont(size)
                .deriveFont(style),
            textureLoader
        )

        @JvmStatic
        fun fromLocal(
            name: String,
            size: Float,
            style: Int = Font.PLAIN,
            textureLoader: TextureLoader? = null
        ): CustomFont = CustomFont(Font(name, style, size.toInt()), textureLoader)
    }

    override fun create(
        text: String,
        antiAlias: Boolean,
        fractionalMetrics: Boolean,
        imgWidth: Int,
        imgHeight: Int,
        linearMag: Boolean,
        useMipmap: Boolean,
        qualityLevel: Int,
        offsetPixel: Int,
        scaleFactor: Float,
        textureLoader: TextureLoader?,
        asyncLoad: () -> Boolean
    ): StaticFontRenderer = UnicodeStaticFontRenderer(
        text,
        font,
        antiAlias,
        fractionalMetrics,
        imgWidth,
        imgHeight,
        linearMag,
        useMipmap,
        qualityLevel,
        offsetPixel,
        scaleFactor,
        textureLoader,
        asyncLoad
    )

}

interface StaticFont {

    val font: Font
    val textureLoader: TextureLoader?

    fun create(
        text: String,
        antiAlias: Boolean = true,
        fractionalMetrics: Boolean = false,
        imgWidth: Int = 1024,
        imgHeight: Int = 64,
        linearMag: Boolean = false,
        useMipmap: Boolean = true,
        qualityLevel: Int = 3,
        offsetPixel: Int = 0,
        scaleFactor: Float = 1f,
        textureLoader: TextureLoader? = this.textureLoader,
        asyncLoad: () -> Boolean = { true }
    ): StaticFontRenderer

    fun lazyCreate(
        text: String,
        antiAlias: Boolean = true,
        fractionalMetrics: Boolean = false,
        imgWidth: Int = 1024,
        imgHeight: Int = 64,
        linearMag: Boolean = false,
        useMipmap: Boolean = true,
        qualityLevel: Int = 3,
        offsetPixel: Int = 0,
        scaleFactor: Float = 1f,
        textureLoader: TextureLoader? = this.textureLoader,
        asyncLoad: () -> Boolean = { true }
    ): Lazy<StaticFontRenderer> = lazy {
        create(
            text,
            antiAlias,
            fractionalMetrics,
            imgWidth,
            imgHeight,
            linearMag,
            useMipmap,
            qualityLevel,
            offsetPixel,
            scaleFactor,
            textureLoader,
            asyncLoad
        )
    }

}