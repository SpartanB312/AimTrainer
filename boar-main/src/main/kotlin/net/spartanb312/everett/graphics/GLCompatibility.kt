package net.spartanb312.everett.graphics

import net.spartanb312.everett.graphics.OpenGL.*
import org.lwjgl.opengl.GLCapabilities

class GLCompatibility(context: GLCapabilities) {

    // OpenGL version
    val glVersion = glGetString(GL_VERSION) ?: ""
    val gpuManufacturer = glGetString(GL_VENDOR) ?: ""
    val gpuName = glGetString(GL_RENDERER)?.substringBefore("/")?: ""
    val intelGraphics = glVersion.lowercase().contains("intel")
            || gpuManufacturer.lowercase().contains("intel")
            || gpuName.lowercase().contains("intel")

    val openGL11 = context.OpenGL11
    val openGL12 = context.OpenGL12
    val openGL13 = context.OpenGL13
    val openGL14 = context.OpenGL14
    val openGL15 = context.OpenGL15
    val openGL20 = context.OpenGL20
    val openGL21 = context.OpenGL21
    val openGL30 = context.OpenGL30
    val openGL31 = context.OpenGL31
    val openGL32 = context.OpenGL32
    val openGL33 = context.OpenGL33
    val openGL40 = context.OpenGL40
    val openGL41 = context.OpenGL41
    val openGL42 = context.OpenGL42
    val openGL43 = context.OpenGL43
    val openGL44 = context.OpenGL44
    val openGL45 = context.OpenGL45
    val openGL46 = context.OpenGL46 || (openGL45 && intelGraphics) //傻逼Intel 草泥马

    // ARB
    val arbShaders: Boolean = !context.OpenGL21
    val arbVbo: Boolean = !context.OpenGL15 && context.GL_ARB_vertex_buffer_object
    val arbMultiTexture: Boolean = context.GL_ARB_multitexture && !context.OpenGL13

    // EXT
    val extBlendFuncSeparate = context.GL_EXT_blend_func_separate && !context.OpenGL14
    val extFramebufferObject = context.GL_EXT_framebuffer_object && !context.OpenGL30

    val openGLVersion = run {
        when {
            openGL46 -> "4.6"
            openGL45 -> "4.5"
            openGL44 -> "4.4"
            openGL43 -> "4.3"
            openGL42 -> "4.2"
            openGL41 -> "4.1"
            openGL40 -> "4.0"
            openGL33 -> "3.3"
            openGL32 -> "3.2"
            openGL31 -> "3.1"
            openGL30 -> "3.0"
            openGL21 -> "2.1"
            openGL20 -> "2.0"
            openGL15 -> "1.5"
            openGL14 -> "1.4"
            openGL13 -> "1.3"
            openGL12 -> "1.2"
            openGL11 -> "1.1"
            else -> throw Exception("Unsupported graphics card")
        }
    }

}