package net.spartanb312.boar.graphics.cache

interface CacheSavable {
    val serialCode: String
    fun saveCache(savePath: String = "disk_cache/$serialCode/"): Boolean
    fun readCache(readPath: String = "disk_cache/$serialCode/"): Boolean
}

fun <T : CacheSavable> T.register(manager: CacheManager): T {
    manager.register(this)
    return this
}

fun <T : CacheSavable> T.unregister(manager: CacheManager): T {
    manager.unregister(this)
    return this
}