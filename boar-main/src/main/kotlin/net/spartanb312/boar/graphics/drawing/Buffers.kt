package net.spartanb312.boar.graphics.drawing

import net.spartanb312.boar.utils.color.ColorRGB

object Buffers {

    fun Int.drawBuffer(format: VertexFormat, block: Buffers.() -> Unit) {

    }

    fun v2f(x: Float, y: Float) {

    }

    fun v3f(x: Float, y: Float, z: Float) {

    }

    fun v2d(x: Double, y: Double) {

    }

    fun v3d(x: Double, y: Double, z: Double) {

    }

    fun v2i(x: Int, y: Int) {

    }

    fun v3i(x: Int, y: Int, z: Int) {

    }

    fun tex(u: Float, v: Float) {

    }

    fun color(colorRGB: ColorRGB = ColorRGB.WHITE) {

    }

    fun color(r: Int, g: Int, b: Int, a: Int = 255) {

    }

    fun color(r: Float, g: Float, b: Float, a: Float = 1f) =
        color((r * 255).toInt(), (g * 255).toInt(), (b * 255).toInt(), (a * 255).toInt())

    fun v2fc(x: Float, y: Float, colorRGB: ColorRGB = ColorRGB.WHITE) {
        v2f(x, y)
        color(colorRGB)
    }

    fun v3fc(x: Float, y: Float, z: Float, colorRGB: ColorRGB = ColorRGB.WHITE) {
        v3f(x, y, z)
        color(colorRGB)
    }

    fun v2dc(x: Double, y: Double, colorRGB: ColorRGB = ColorRGB.WHITE) {
        v2d(x, y)
        color(colorRGB)
    }

    fun v3dc(x: Double, y: Double, z: Double, colorRGB: ColorRGB = ColorRGB.WHITE) {
        v3d(x, y, z)
        color(colorRGB)
    }

    fun v2ic(x: Int, y: Int, colorRGB: ColorRGB = ColorRGB.WHITE) {
        v2i(x, y)
        color(colorRGB)
    }

    fun v3ic(x: Int, y: Int, z: Int, colorRGB: ColorRGB = ColorRGB.WHITE) {
        v3i(x, y, z)
        color(colorRGB)
    }

    fun v2Tex2fC(x: Float, y: Float, u: Float, v: Float, colorRGB: ColorRGB = ColorRGB.WHITE) {
        v2f(x, y)
        color(colorRGB)
        tex(u, v)
    }

    fun v2Tex2dC(x: Double, y: Double, u: Float, v: Float, colorRGB: ColorRGB = ColorRGB.WHITE) {
        v2d(x, y)
        color(colorRGB)
        tex(u, v)
    }

    fun v2Tex2iC(x: Int, y: Int, u: Float, v: Float, colorRGB: ColorRGB = ColorRGB.WHITE) {
        v2i(x, y)
        color(colorRGB)
        tex(u, v)
    }

    fun v3Tex2fC(
        x: Float,
        y: Float,
        z: Float,
        u: Float,
        v: Float,
        colorRGB: ColorRGB = ColorRGB.WHITE,
    ) {
        v3f(x, y, z)
        color(colorRGB)
        tex(u, v)
    }

    fun v3Tex2dC(
        x: Double,
        y: Double,
        z: Double,
        u: Float,
        v: Float,
        colorRGB: ColorRGB = ColorRGB.WHITE,
    ) {
        v3d(x, y, z)
        color(colorRGB)
        tex(u, v)
    }

    fun v3Tex2iC(x: Int, y: Int, z: Int, u: Float, v: Float, colorRGB: ColorRGB = ColorRGB.WHITE) {
        v3i(x, y, z)
        color(colorRGB)
        tex(u, v)
    }

}