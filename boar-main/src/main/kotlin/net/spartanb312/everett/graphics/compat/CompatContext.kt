package net.spartanb312.everett.graphics.compat

import net.spartanb312.everett.graphics.compat.multitex.IMultiTexture
import net.spartanb312.everett.graphics.compat.shader.IShaders
import net.spartanb312.everett.graphics.compat.vao.IVAO
import net.spartanb312.everett.graphics.compat.vbo.IVBO
import net.spartanb312.everett.graphics.compat.vertexattrib.IVertexAttrib

interface CompatContext : IMultiTexture, IShaders, IVAO, IVBO, IVertexAttrib