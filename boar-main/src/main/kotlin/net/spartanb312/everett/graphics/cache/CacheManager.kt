package net.spartanb312.everett.graphics.cache

import net.spartanb312.everett.game.Language
import net.spartanb312.everett.utils.config.Configurable
import java.util.concurrent.CopyOnWriteArrayList

open class CacheManager(name: String) : Configurable(name, Language) {
    private val saveList = CopyOnWriteArrayList<CacheSavable>()
    fun register(cacheSavable: CacheSavable) = saveList.add(cacheSavable)
    fun unregister(cacheSavable: CacheSavable) = saveList.remove(cacheSavable)
    fun saveCache(path: String? = null) = saveList.forEach { it.saveCache(path ?: "disk_cache/${it.serialCode}/") }
    fun readCache(path: String? = null) = saveList.forEach { it.readCache(path ?: "disk_cache/${it.serialCode}/") }
}