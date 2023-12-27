package net.spartanb312.boar.game.render

import net.spartanb312.boar.graphics.cache.CacheManager
import net.spartanb312.boar.graphics.font.CustomFont
import net.spartanb312.boar.graphics.font.FontRenderer
import net.spartanb312.boar.graphics.font.SFR
import net.spartanb312.boar.graphics.font.StaticFont

object FontCacheManager : CacheManager() {
    init {
        register(FontRendererMain)
        register(SF)
    }
}

object FontRendererMain : FontRenderer by FontRenderer(
    "assets/font/Microsoft YaHei UI.ttc",
    50f,
    scaleFactor = 0.5f,
    chunkSize = 49,
    textureLoader = TextureManager
)

object FontMain : StaticFont by CustomFont.fromPath(
    "assets/font/Microsoft YaHei UI.ttc",
    50f,
    textureLoader = TextureManager
)

object SF : SFR by FontMain.create("Bandit Evo")