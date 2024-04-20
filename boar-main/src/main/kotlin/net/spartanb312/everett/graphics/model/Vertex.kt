package net.spartanb312.everett.graphics.model

import net.spartanb312.everett.utils.math.vector.Vec2f
import net.spartanb312.everett.utils.math.vector.Vec3f

/**
 * Size = (3+3+2) * 4 = 32bytes
 */
data class Vertex(
    val position: Vec3f,
    val normal: Vec3f,
    val texCoords: Vec2f,
    val tangent:Vec3f,
    val bitangent:Vec3f
)