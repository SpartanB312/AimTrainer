package net.spartanb312.everett.graphics

import net.spartanb312.everett.utils.math.vector.Vec3f
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object Sphere {

    fun generate(zSegments: Int, xSegments: Int): Pair<List<Vec3f>, List<Vec3f>> { // Vert, Normal (4:1)
        val vertices = mutableListOf<Vec3f>()
        val normals = mutableListOf<Vec3f>()
        val zAngle = (PI / zSegments).toFloat()
        val xAngle = (2 * PI / (xSegments - 1)).toFloat()
        var r: Float
        var x: Float
        var y: Float
        var z: Float
        var count = 0
        for (zStep in 0 until zSegments) {
            for (xStep in 0 until xSegments) {
                z = cos(zStep * zAngle)
                r = sqrt(1f - z * z)
                x = r * cos(xStep * xAngle)
                y = r * sin(xStep * xAngle)
                val vec1 = Vec3f(x, y, z)

                z = cos((zStep + 1) * zAngle)
                r = sqrt(1f - z * z)
                x = r * cos(xStep * xAngle)
                y = r * sin(xStep * xAngle)
                val vec2 = Vec3f(x, y, z)

                z = cos((zStep + 1) * zAngle)
                r = sqrt(1f - z * z)
                x = r * cos((xStep + 1) * xAngle)
                y = r * sin((xStep + 1) * xAngle)
                val vec3 = Vec3f(x, y, z)

                z = cos(zStep * zAngle)
                r = sqrt(1f - z * z)
                x = r * cos((xStep + 1) * xAngle)
                y = r * sin((xStep + 1) * xAngle)
                val vec4 = Vec3f(x, y, z)

                val a1 = Vec3f(vec2.x - vec1.x, vec2.y - vec1.y, vec2.z - vec1.z)
                val a2 = if (zStep == zSegments - 1) Vec3f(vec4.x - vec2.x, vec4.y - vec2.y, vec4.z - vec2.z)
                else Vec3f(vec3.x - vec2.x, vec3.y - vec2.y, vec3.z - vec2.z)
                val a3 = Vec3f(a1.y * a2.z - a2.y * a1.z, a1.z * a2.x - a2.z * a1.x, a1.x * a2.y - a2.x * a1.y)
                val a = 1f / sqrt(a3.x * a3.x + a3.y * a3.y + a3.z * a3.z)

                if (count % xSegments != 0) {
                    normals.add(Vec3f(a3.x * a, a3.y * a, a3.z * a))
                    // For triangle strip
                    vertices.add(vec1)
                    vertices.add(vec2)
                    vertices.add(vec4)
                    vertices.add(vec3)
                }
                count++
            }
        }
        return Pair(vertices, normals)
    }

}