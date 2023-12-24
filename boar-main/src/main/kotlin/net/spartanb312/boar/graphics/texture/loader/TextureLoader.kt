package net.spartanb312.boar.graphics.texture.loader

import java.util.concurrent.LinkedBlockingQueue

interface TextureLoader {
    val queue: LinkedBlockingQueue<LazyTextureContainer>
    fun renderThreadHook(timeLimit: Int = -1)
    fun add(texture: LazyTextureContainer)
    fun resume()
    fun suspend()
    fun stop()
    fun remaining() = queue.size
    fun LazyTextureContainer.register(): LazyTextureContainer {
        add(this)
        return this
    }
}