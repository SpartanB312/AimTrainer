package net.spartanb312.boar.graphics.cache

import java.util.concurrent.CopyOnWriteArrayList

open class CacheManager {
    private val saveList = CopyOnWriteArrayList<CacheSavable>()
    fun register(cacheSavable: CacheSavable) = saveList.add(cacheSavable)
    fun unregister(cacheSavable: CacheSavable) = saveList.remove(cacheSavable)
    fun saveCache(path: String? = null) = saveList.forEach { it.saveCache(path ?: "disk_cache/${it.serialCode}/") }
    fun readCache(path: String? = null) = saveList.forEach { it.readCache(path ?: "disk_cache/${it.serialCode}/") }
}