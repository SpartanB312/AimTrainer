package net.spartanb312.everett.graphics.framebuffer

import net.spartanb312.everett.utils.color.ColorRGB

class ResizableFramebuffer(private var delegate: FixedFramebuffer) : Framebuffer {

    constructor(
        width: Int,
        height: Int,
        useRBO: Boolean,
        clearColor: ColorRGB = ColorRGB(0f, 0f, 0f, 0f)
    ) : this(FixedFramebuffer(width, height, useRBO, clearColor))

    override val width get() = delegate.width
    override val height get() = delegate.height
    override val useRBO get() = delegate.useRBO
    override val clearColor get() = delegate.clearColor
    override val fbo get() = delegate.fbo
    override val rbo get() = delegate.rbo
    override val colorAttachments = mutableListOf<Framebuffer.ColorLayer>()

    fun generateColorLayer(): ResizableColorLayer {
        val wrappedLayer = ResizableColorLayer(delegate.generateColorLayer())
        colorAttachments.add(wrappedLayer)
        return wrappedLayer
    }

    fun resize(width: Int, height: Int): FixedFramebuffer {
        val new = FixedFramebuffer(width, height, useRBO, clearColor)
        delegate.delete(false)
        delegate = new
        colorAttachments.forEach {
            if (it is ResizableColorLayer) {
                it.replace(new.generateColorLayer())
            }
        }
        return new
    }

    class ResizableColorLayer(private var delegate: FixedFramebuffer.FixedColorLayer) : Framebuffer.ColorLayer {

        override val texture get() = delegate.texture
        override val framebuffer get() = delegate.framebuffer
        override val index get() = delegate.index

        override val id get() = texture.id
        override val available get() = texture.available
        override var height: Int
            get() = texture.height
            set(value) {
                texture.height = value
            }
        override var width: Int
            get() = texture.width
            set(value) {
                texture.width = value
            }

        override fun bindTexture() = texture.bindTexture()
        override fun unbindTexture() = texture.unbindTexture()
        override fun deleteTexture() = texture.deleteTexture()

        fun replace(new: FixedFramebuffer.FixedColorLayer) {
            delegate.delete()
            delegate = new
        }

    }

}