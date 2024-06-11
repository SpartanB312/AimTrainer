package net.spartanb312.everett.graphics.framebuffer

import net.spartanb312.everett.utils.color.ColorRGB

class ResizableFramebuffer(private var delegate: FixedFramebuffer) : Framebuffer {

    constructor(
        width: Int,
        height: Int,
        useRBO: Boolean,
        clearColor: ColorRGB = ColorRGB(0f, 0f, 0f, 1f)
    ) : this(FixedFramebuffer(width, height, useRBO, clearColor))

    override val width get() = delegate.width
    override val height get() = delegate.height
    override val useRBO get() = delegate.useRBO
    override val clearColor get() = delegate.clearColor
    override val fbo get() = delegate.fbo
    override val rbo get() = delegate.rbo
    override val texture get() = delegate.texture

    fun resize(width: Int, height: Int): FixedFramebuffer {
        val new = FixedFramebuffer(width, height, useRBO, clearColor)
        delegate.delete()
        delegate = new
        return new
    }

}