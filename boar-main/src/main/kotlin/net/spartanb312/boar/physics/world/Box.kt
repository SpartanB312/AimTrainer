package net.spartanb312.boar.physics.world

import net.spartanb312.boar.utils.math.vector.Vec3d

interface Box {
    var minX: Double
    var minY: Double
    var minZ: Double
    var maxX: Double
    var maxY: Double
    var maxZ: Double

    fun contains(vec3d: Vec3d) = contains(vec3d.x, vec3d.y, vec3d.z)

    fun contains(x: Double, y: Double, z: Double): Boolean {
        return x > minX && y > minY && z > minZ &&
                x < maxX && y < maxY && z < maxZ
    }

    fun contains(box: Box): Boolean {
        return box.minX > minX &&
                box.minY > minY &&
                box.minZ > minZ &&
                box.maxX < maxX &&
                box.maxY < maxY &&
                box.maxZ < maxZ
    }

    fun intersects(box: Box): Boolean {
        return box.minX < maxX &&
                box.minY < maxY &&
                box.minZ < maxZ &&
                box.maxX > minX &&
                box.maxY > minY &&
                box.maxZ > minZ
    }

}

class BoundingBox(
    override var minX: Double,
    override var minY: Double,
    override var minZ: Double,
    override var maxX: Double,
    override var maxY: Double,
    override var maxZ: Double,
) : Box {
    constructor(minVec: Vec3d, maxVec: Vec3d) : this(minVec.x, minVec.y, minVec.z, maxVec.x, maxVec.y, maxVec.z)
}