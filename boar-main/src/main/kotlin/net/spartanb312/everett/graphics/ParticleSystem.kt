package net.spartanb312.everett.graphics

import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.graphics.drawing.VertexFormat
import net.spartanb312.everett.graphics.drawing.buffer.ArrayedVertexBuffer.buffer
import net.spartanb312.everett.graphics.drawing.buffer.PersistentMappedVertexBuffer
import net.spartanb312.everett.graphics.drawing.buffer.PersistentMappedVertexBuffer.draw
import net.spartanb312.everett.utils.color.ColorHSB
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.math.vector.Vec2f
import net.spartanb312.everett.utils.math.vector.distanceSq
import net.spartanb312.everett.utils.timing.Timer
import org.lwjgl.opengl.GL11.GL_POINTS
import org.lwjgl.opengl.GL11.glPointSize
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.sqrt

class ParticleSystem(
    private val speed: Float = 0.01f,
    private val amount: Int = 150,
    private val lineRadius: Float = 200f,
    private val delta: Int = 10,
    private val followMouse: Boolean = true,
    private val rainbowColor: Boolean = false,
    private val initialColor: ColorRGB = ColorRGB.WHITE,
    instantInit: Boolean = true
) {

    private val lineRadiusSq = lineRadius * lineRadius
    private val initialAlpha = if (rainbowColor) 255 else initialColor.a

    private val tickTimer = Timer()
    private val startTime = System.currentTimeMillis()
    private val particleList: MutableList<Particle> = ArrayList()
    private val particles = mutableMapOf<Float, MutableList<Particle>>()
    private var drawLines = true
    val initialized = AtomicBoolean(false)
    var hue = 0

    init {
        if (instantInit) {
            init()
        }
    }

    fun init() {
        if (initialized.getAndSet(true)) return
        //println("Initializing")
        repeat(amount) {
            particleList.add(generateParticle())
        }
        particleList.forEach {
            particles.getOrPut(it.size) { mutableListOf() }.add(it)
        }
    }

    private fun tick() {
        hue = (((System.currentTimeMillis() - startTime).toInt() % 10000 / 10000F) * 255).toInt().coerceIn(0, 255)
        for (particle in particleList) {
            particle.tick(delta, speed, hue)
        }
    }

    fun render() {
        if (!initialized.get()) init()
        tickTimer.passedAndReset(10) { tick() }
        for (size in particles.keys) {
            val list = particles[size]!!
            glPointSize(size)
            GLHelper.pointSmooth = true
            if (!RS.compatMode) GL_POINTS.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
                list.forEach {
                    universe(it.x, it.y, it.color.alpha(it.alpha.toInt().coerceIn(0, initialAlpha)))
                }
            } else GL_POINTS.buffer(VertexFormat.Pos2fColor, particleList.size) {
                list.forEach {
                    v2fc(it.x, it.y, it.color.alpha(it.alpha.toInt().coerceIn(0, initialAlpha)))
                }
            }
        }
        var count = 0
        for (particle in particleList) {
            if (distanceSq(
                    if (followMouse) RS.mouseXF else RS.widthF / 2f,
                    if (followMouse) RS.mouseYF else RS.heightF / 2f,
                    particle.x,
                    particle.y
                ) > lineRadiusSq
            ) continue
            var nearestDistance = Float.MAX_VALUE
            var nearestParticle: Particle? = null
            for (otherParticle in particleList) {
                if (otherParticle == particle) continue
                val distance = particle.getDistanceSqTo(otherParticle)
                if (distance > lineRadiusSq) continue
                if (distance >= nearestDistance) continue
                if (distanceSq(
                        if (followMouse) RS.mouseXF else RS.widthF / 2f,
                        if (followMouse) RS.mouseYF else RS.heightF / 2f,
                        otherParticle.x,
                        otherParticle.y
                    ) > lineRadiusSq
                ) continue
                nearestDistance = distance
                nearestParticle = otherParticle
            }
            if (nearestParticle == null || !drawLines) continue
            val alpha = (1.0f - sqrt(nearestDistance) / lineRadius).coerceIn(0.0f, 1.0f)
            RenderUtils.drawLine(
                particle.x, particle.y,
                nearestParticle.x, nearestParticle.y,
                1f,
                particle.color.alpha((initialAlpha * alpha).toInt()),
                nearestParticle.color.alpha((initialAlpha * alpha).toInt())
            )
            count += 2
        }
    }

    inner class Particle(private var velocity: Vec2f, x: Float, y: Float, var size: Float, private val hue: Int) {

        var color = initialColor
        private var pos: Vec2f = Vec2f(x, y)
        var alpha = 0f; private set

        fun getDistanceSqTo(particle1: Particle): Float = getDistanceSqTo(particle1.x, particle1.y)

        private fun getDistanceSqTo(f: Float, f2: Float): Float = distanceSq(x, y, f, f2)

        var x: Float
            get() = pos.x
            set(f) {
                pos = Vec2f(f, pos.y)
            }

        var y: Float
            get() = pos.y
            set(f) {
                pos = Vec2f(pos.x, f)
            }

        fun tick(delta: Int, speed: Float, hue: Int) {
            x += velocity.x * delta * speed
            y += velocity.y * delta * speed
            if (alpha < initialAlpha) alpha += 0.05f * delta
            if (pos.x > RS.width) x = 0f
            if (pos.x < 0) x = RS.widthF
            if (pos.y > RS.height) y = 0f
            if (pos.y < 0) y = RS.heightF
            if (rainbowColor) color = ColorHSB((hue + this.hue) % 255, 128, 240).toRGB()
        }

    }

    private fun generateParticle(): Particle {
        val velocity = Vec2f((Math.random() * 2.0f - 1.0f).toFloat(), (Math.random() * 2.0f - 1.0f).toFloat())
        val x = Random().nextInt(RS.width).toFloat()
        val y = Random().nextInt(RS.height).toFloat()
        val size = (Math.random() * 5.0f).toFloat() + 2.0f
        return Particle(velocity, x, y, size, kotlin.random.Random.nextInt(0, 32))
    }

}