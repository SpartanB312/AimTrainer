package net.spartanb312.everett.game.render

import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.shader.GLSLSandboxShader

class GLSLSandbox(fragShaderPath: String) : GLSLSandboxShader(
    if (RS.compatMode) "assets/shader/sandbox/210/DefaultVertex.vsh"
    else "assets/shader/sandbox/450/DefaultVertex.vsh",
    fragShaderPath
)