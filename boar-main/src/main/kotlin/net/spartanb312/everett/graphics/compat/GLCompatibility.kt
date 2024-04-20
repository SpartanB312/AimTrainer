package net.spartanb312.everett.graphics.compat

import net.spartanb312.everett.graphics.OpenGL.*
import org.lwjgl.opengl.GLCapabilities

class GLCompatibility(context: GLCapabilities) {

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
    val openGL46 = context.OpenGL46

    // ARB
    val arbShaders: Boolean = !context.OpenGL21
    val arbVbo: Boolean = !context.OpenGL15 && context.GL_ARB_vertex_buffer_object
    val arbMultiTexture: Boolean = context.GL_ARB_multitexture && !context.OpenGL13

    // EXT
    val extBlendFuncSeparate = context.GL_EXT_blend_func_separate && !context.OpenGL14
    val extFramebufferObject = context.GL_EXT_framebuffer_object && !context.OpenGL30

    // Features
    val blendSeparate = context.OpenGL14 || context.GL_EXT_blend_func_separate
    val useVAO = context.OpenGL30 || context.GL_ARB_vertex_array_object

    // OpenGL version
    val glVersion = glGetString(GL_VERSION) ?: ""
    val gpuManufacturer = glGetString(GL_VENDOR) ?: ""
    val gpuName = glGetString(GL_RENDERER)?.substringBefore("/")?: ""
    val intelGraphics = glVersion.lowercase().contains("intel")
            || gpuManufacturer.lowercase().contains("intel")
            || gpuName.lowercase().contains("intel")

    val openGLVersion = context.run {
        when {
            OpenGL46 -> "4.6"
            OpenGL45 -> "4.5"
            OpenGL44 -> "4.4"
            OpenGL43 -> "4.3"
            OpenGL42 -> "4.2"
            OpenGL41 -> "4.1"
            OpenGL40 -> "4.0"
            OpenGL33 -> "3.3"
            OpenGL32 -> "3.2"
            OpenGL31 -> "3.1"
            OpenGL30 -> "3.0"
            OpenGL21 -> "2.1"
            OpenGL20 -> "2.0"
            OpenGL15 -> "1.5"
            OpenGL14 -> "1.4"
            OpenGL13 -> "1.3"
            OpenGL12 -> "1.2"
            OpenGL11 -> "1.1"
            else -> throw Exception("Unsupported graphics card")
        }
    }

}