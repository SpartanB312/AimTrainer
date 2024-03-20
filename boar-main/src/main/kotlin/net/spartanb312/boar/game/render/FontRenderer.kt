package net.spartanb312.boar.game.render

import net.spartanb312.boar.game.config.setting.ListSetting
import net.spartanb312.boar.game.option.impls.AccessibilityOption
import net.spartanb312.boar.graphics.cache.CacheManager
import net.spartanb312.boar.graphics.font.CustomFont
import net.spartanb312.boar.graphics.font.FontRenderer
import net.spartanb312.boar.graphics.font.StaticFont

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

object FontRendererMain : FontRenderer by FontRenderer(
    "assets/font/Microsoft YaHei UI.ttc",
    50f,
    scaleFactor = 0.5f,
    chunkSize = 49,
    textureLoader = TextureManager
)

object FontRendererBig : FontRenderer by FontRenderer(
    "assets/font/Microsoft YaHei UI.ttc",
    50f,
    scaleFactor = 1f,
    chunkSize = 49,
    textureLoader = TextureManager
)

object FontRendererROG : FontRenderer by FontRenderer(
    "assets/font/ROG.otf",
    100f,
    scaleFactor = 1f,
    imgSize = 1024,
    chunkSize = 49,
    textureLoader = TextureManager
)

object FontRendererASCII : FontRenderer by FontRenderer(
    "assets/font/Microsoft YaHei UI.ttc",
    25f,
    imgSize = 256,
    chunkSize = 49
)

object FontMain : StaticFont by CustomFont.fromPath(
    "assets/font/Microsoft YaHei UI.ttc",
    50f,
    textureLoader = TextureManager
)