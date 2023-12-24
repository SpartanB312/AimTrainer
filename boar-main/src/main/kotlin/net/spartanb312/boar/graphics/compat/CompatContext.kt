package net.spartanb312.boar.graphics.compat

import net.spartanb312.boar.graphics.compat.multitex.IMultiTexture
import net.spartanb312.boar.graphics.compat.shader.IShaders
import net.spartanb312.boar.graphics.compat.vao.IVAO
import net.spartanb312.boar.graphics.compat.vbo.IVBO
import net.spartanb312.boar.graphics.compat.vertexattrib.IVertexAttrib

interface CompatContext : IMultiTexture, IShaders, IVAO, IVBO, IVertexAttrib