package net.spartanb312.everett.game.render

import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer.draw
import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.math.ceilToInt
import org.lwjgl.opengl.GL11

object HitEffectsRenderer {

    private val tasks = mutableListOf<RenderTask>()

    var hitCounter = 0
    var killCounter = 0

    fun acceptCount(radius: Float, scale: Float, duration: Int) {
        if (hitCounter > 0) {
            hitCounter--
            addHitEffect(radius, scale, duration)
        }
        if (killCounter > 0) {
            killCounter--
            addKillEffect(radius, scale, duration)
        }
    }

    fun onRender(stack: MatrixLayerStack, colorRGB: ColorRGB) {
        val now = System.currentTimeMillis()
        tasks.removeIf { now >= it.endTime }
        tasks.forEach { it.job.invoke(stack, colorRGB) }
    }

    class RenderTask(val job: MatrixLayerStack.(ColorRGB) -> Unit, val endTime: Long)

    enum class HitEffect(val block: MatrixLayerStack.(Float, Float, ColorRGB, Long) -> Unit) {
        Kill({ radius, scale, color, endTime ->
            val stage = ((endTime - System.currentTimeMillis()).toInt() / 400f).coerceIn(0f..1f)
            val rate = if (stage > 0.6f) (1f - stage) / 0.4f else stage / 0.6f
            val width = 7f * scale * rate
            val height = 35f * scale * rate
            GL11.GL_TRIANGLES.draw(PersistentMappedVertexBuffer.VertexMode.Universal) {
                // Up
                universal(width, 0f, color.alpha(160))
                universal(0f, radius + height, color.alpha(64))
                universal(-width, 0f, color.alpha(160))
                // Down
                universal(-width, 0f, color.alpha(160))
                universal(0f, -radius - height, color.alpha(64))
                universal(width, 0f, color.alpha(160))
                // Left
                universal(0f, -width, color.alpha(160))
                universal(-radius - height, 0f, color.alpha(64))
                universal(0f, width, color.alpha(160))
                // Right
                universal(0f, -width, color.alpha(72))
                universal(radius + height, 0f, color.alpha(64))
                universal(0f, width, color.alpha(160))
            }
        }),
        Hit({ radius, scale, color, endTime ->
            val rate = ((endTime - System.currentTimeMillis()).toInt() / 500f).coerceIn(0f..1f)
            val width = 5f * scale * rate
            val height = 20f * scale * rate
            GL11.GL_TRIANGLES.draw(PersistentMappedVertexBuffer.VertexMode.Universal) {
                // Up
                universal(width, radius, color.alpha(72))
                universal(0f, radius + height, color.alpha(16))
                universal(-width, radius, color.alpha(72))
                // Down
                universal(-width, -radius, color.alpha(72))
                universal(0f, -radius - height, color.alpha(16))
                universal(width, -radius, color.alpha(72))
                // Left
                universal(-radius, -width, color.alpha(72))
                universal(-radius - height, 0f, color.alpha(16))
                universal(-radius, width, color.alpha(72))
                // Right
                universal(radius, -width, color.alpha(72))
                universal(radius + height, 0f, color.alpha(16))
                universal(radius, width, color.alpha(72))
            }
        })
    }

    fun addKillEffect(radius: Float, scale: Float, duration: Int) {
        val endTime = System.currentTimeMillis() + (duration * 0.8f).ceilToInt()
        val job: MatrixLayerStack.(ColorRGB) -> Unit = {
            HitEffect.Kill.block.invoke(this, radius, scale, ColorRGB(255, 72, 72), endTime)
        }
        tasks.add(RenderTask(job, endTime))
    }

    fun addHitEffect(radius: Float, scale: Float, duration: Int) {
        val endTime = System.currentTimeMillis() + duration
        val job: MatrixLayerStack.(ColorRGB) -> Unit = {
            HitEffect.Hit.block.invoke(this, radius, scale, it, endTime)
        }
        tasks.add(RenderTask(job, endTime))
    }

}