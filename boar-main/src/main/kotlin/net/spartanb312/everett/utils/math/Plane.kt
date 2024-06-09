package net.spartanb312.everett.utils.math

import net.spartanb312.everett.utils.math.vector.Vec3f

class Plane(val normal: Vec3f) {

    var valid = true; private set

    constructor(vec1: Vec3f, vec2: Vec3f) : this(vec1 cross vec2) {
        valid = vec1.normalize() != vec2.normalize()
    }

    // Return the projection of the input on the plane
    fun projectOnPlane(vec: Vec3f): Vec3f {
        return if (!valid) vec
        else {
            val num1 = normal dot normal
            val num2 = vec dot normal
            Vec3f(
                vec.x - normal.x * num2 / num1,
                vec.y - normal.y * num2 / num1,
                vec.z - normal.z * num2 / num1
            )
        }
    }

}