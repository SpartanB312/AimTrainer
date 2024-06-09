package net.spartanb312.everett.game.render

import net.spartanb312.everett.graphics.shader.GLSLSandboxShader

class GLSLSandbox(fragShaderPath: String) : GLSLSandboxShader(
    "assets/shader/sandbox/DefaultVertex.vsh",
    fragShaderPath
)