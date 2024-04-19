package net.spartanb312.boar.game.render

import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.shader.GLSLSandboxShader

class GLSLSandbox(fragShaderPath: String) : GLSLSandboxShader(
    if (RS.compatMode) "assets/shader/sandbox/210/DefaultVertex.vsh"
    else "assets/shader/sandbox/450/DefaultVertex.vsh",
    fragShaderPath
)