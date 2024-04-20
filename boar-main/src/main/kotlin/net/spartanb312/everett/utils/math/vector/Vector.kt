@file:Suppress("NOTHING_TO_INLINE")

package net.spartanb312.everett.utils.math.vector

// Conversion
inline fun Vec2f.toVec2d(): Vec2d = Vec2d(x, y)

inline fun Vec2f.toVec2i(): Vec2i = Vec2i(x, y)

inline fun Vec2f.toVec3f(z: Number = 0.0): Vec3f = Vec3f(x, y, z)

inline fun Vec2f.toVec3d(z: Number = 0.0): Vec3d = Vec3d(x, y, z)

inline fun Vec2f.toVec3i(z: Number = 0.0): Vec3i = Vec3i(x, y, z)

inline fun Vec2d.toVec2f(): Vec2f = Vec2f(x, y)

inline fun Vec2d.toVec2i(): Vec2i = Vec2i(x, y)

inline fun Vec2d.toVec3f(z: Number = 0.0): Vec3f = Vec3f(x, y, z)

inline fun Vec2d.toVec3d(z: Number = 0.0): Vec3d = Vec3d(x, y, z)

inline fun Vec2d.toVec3i(z: Number = 0.0): Vec3i = Vec3i(x, y, z)

inline fun Vec2i.toVec2f(): Vec2f = Vec2f(x, y)

inline fun Vec2i.toVec2d(): Vec2d = Vec2d(x, y)

inline fun Vec2i.toVec3f(z: Number = 0.0): Vec3f = Vec3f(x, y, z)

inline fun Vec2i.toVec3d(z: Number = 0.0): Vec3d = Vec3d(x, y, z)

inline fun Vec2i.toVec3i(z: Number = 0.0): Vec3i = Vec3i(x, y, z)

inline fun Vec3f.toVec2f(): Vec2f = Vec2f(x, y)

inline fun Vec3f.toVec2d(): Vec2d = Vec2d(x, y)

inline fun Vec3f.toVec2i(): Vec2i = Vec2i(x, y)

inline fun Vec3f.toVec3d(): Vec3d = Vec3d(x, y, z)

inline fun Vec3f.toVec3i(): Vec3i = Vec3i(x, y, z)

inline fun Vec3d.toVec2f(): Vec2f = Vec2f(x, y)

inline fun Vec3d.toVec2d(): Vec2d = Vec2d(x, y)

inline fun Vec3d.toVec2i(): Vec2i = Vec2i(x, y)

inline fun Vec3d.toVec3f(): Vec3f = Vec3f(x, y, z)

inline fun Vec3d.toVec3i(): Vec3i = Vec3i(x, y, z)

inline fun Vec3i.toVec2f(): Vec2f = Vec2f(x, y)

inline fun Vec3i.toVec2d(): Vec2d = Vec2d(x, y)

inline fun Vec3i.toVec2i(): Vec2i = Vec2i(x, y)

inline fun Vec3i.toVec3f(): Vec3f = Vec3f(x, y, z)

inline fun Vec3i.toVec3d(): Vec3d = Vec3d(x, y, z)

// Plane distance
inline fun Vec2f.distanceTo(vec2f: Vec2f): Double = distance(x, y, vec2f.x, vec2f.y)

inline fun Vec2f.distanceTo(vec2d: Vec2d): Double = distance(xDouble, yDouble, vec2d.x, vec2d.y)

inline fun Vec2f.distanceTo(vec2i: Vec2i): Double = distance(x, y, vec2i.xFloat, vec2i.yFloat)

inline fun Vec2d.distanceTo(vec2f: Vec2f): Double = distance(x, y, vec2f.xDouble, vec2f.yDouble)

inline fun Vec2d.distanceTo(vec2d: Vec2d): Double = distance(x, y, vec2d.x, vec2d.y)

inline fun Vec2d.distanceTo(vec2i: Vec2i): Double = distance(x, y, vec2i.xDouble, vec2i.yDouble)

inline fun Vec2i.distanceTo(vec2f: Vec2f): Double = distance(xFloat, yFloat, vec2f.x, vec2f.y)

inline fun Vec2i.distanceTo(vec2d: Vec2d): Double = distance(xDouble, yDouble, vec2d.x, vec2d.y)

inline fun Vec2i.distanceTo(vec2i: Vec2i): Double = distance(x, y, vec2i.x, vec2i.y)

// Spatial distance
inline fun Vec3f.distanceTo(t: Vec3f): Double = distance(x, y, z, t.x, t.y, t.z)

inline fun Vec3f.distanceTo(t: Vec3d): Double = distance(xDouble, yDouble, zDouble, t.x, t.y, t.z)

inline fun Vec3f.distanceTo(t: Vec3i): Double = distance(x, y, z, t.xFloat, t.yFloat, t.zFloat)

inline fun Vec3d.distanceTo(t: Vec3f): Double = distance(x, y, z, t.xDouble, t.yDouble, t.zDouble)

inline fun Vec3d.distanceTo(t: Vec3d): Double = distance(x, y, z, t.x, t.y, t.z)

inline fun Vec3d.distanceTo(t: Vec3i): Double = distance(x, y, z, t.xDouble, t.yDouble, t.zDouble)

inline fun Vec3i.distanceTo(t: Vec3f): Double = distance(xFloat, yFloat, zFloat, t.x, t.y, t.z)

inline fun Vec3i.distanceTo(t: Vec3d): Double = distance(xDouble, yDouble, zDouble, t.x, t.y, t.z)

inline fun Vec3i.distanceTo(t: Vec3i): Double = distance(x, y, z, t.x, t.y, t.z)