package net.spartanb312.everett.game.render

import net.spartanb312.everett.game.option.impls.AccessibilityOption
import net.spartanb312.everett.graphics.cache.CacheManager
import net.spartanb312.everett.graphics.font.FontRenderer
import net.spartanb312.everett.graphics.font.RTOFontRenderer
import net.spartanb312.everett.utils.config.setting.ListSetting
import java.awt.Font

object FontCacheManager : CacheManager("FontRenderer") {

    private var main by FontRendererMain.reg()
    private var big by FontRendererBig.reg()
    private var rog by FontRendererROG.reg()

    private fun FontRenderer.reg(): ListSetting {
        register(this)
        return setting(serialCode, listOf())
    }

    fun syncChunks() {
        main = FontRendererMain.loadedChunks.map { it.toString() }
        big = FontRendererBig.loadedChunks.map { it.toString() }
        rog = FontRendererROG.loadedChunks.map { it.toString() }
    }

    fun initChunks() {
        if (AccessibilityOption.chunkCache) {
            main.forEach { FontRendererMain.initChunkForce(it.toInt()) }
            big.forEach { FontRendererBig.initChunkForce(it.toInt()) }
            rog.forEach { FontRendererROG.initChunkForce(it.toInt()) }
        }
    }

}


object FontRendererMain : FontRenderer by RTOFontRenderer(
    "assets/font/Microsoft YaHei UI.ttc",
    50f,
    scaleFactor = 0.5f,
    chunkSize = 49,
    textureLoader = TextureManager
)

object FontRendererBig : FontRenderer by RTOFontRenderer(
    "assets/font/Microsoft YaHei UI.ttc",
    50f,
    scaleFactor = 1f,
    chunkSize = 49,
    textureLoader = TextureManager
)

object FontRendererBold : FontRenderer by FontRenderer(
    "assets/font/Microsoft YaHei UI.ttc",
    25f,
    scaleFactor = 0.5f,
    chunkSize = 49,
    textureLoader = TextureManager,
    style = Font.BOLD,
    useMipmap = false
)

object FontRendererROG : FontRenderer by FontRenderer(
    "assets/font/ROG.otf",
    100f,
    scaleFactor = 1f,
    imgSize = 1024,
    chunkSize = 49,
    textureLoader = TextureManager
)

object FontRendererIcon : FontRenderer by FontRenderer(
    "assets/font/Icon.otf",
    50f,
    scaleFactor = 1f,
    chunkSize = 49,
    textureLoader = TextureManager
)

object FontRendererASCII : FontRenderer by FontRenderer(
    "assets/font/Microsoft YaHei UI.ttc",
    25f,
    imgSize = 256,
    chunkSize = 49,
    useMipmap = false
)