package net.spartanb312.everett.game.render.gui


import net.spartanb312.everett.game.render.FontRendererBig
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.utils.Logger
import net.spartanb312.everett.utils.color.ColorRGB

object Notification {

    private val notifications = mutableListOf<Notification>()

    fun showCenter(message: String, duration: Long = 3000, log: Boolean = true) {
        if (log) Logger.info("[Notification-Center] $message")
        notifications.removeIf { it.type == Type.Center }
        notifications.add(Notification(Type.Center, message, duration, System.currentTimeMillis() + duration))
    }

    fun showLeft(message: String, duration: Long = 5000, log: Boolean = true) {
        if (log) Logger.info("[Notification-Left] $message")
        notifications.add(Notification(Type.Center, message, duration, System.currentTimeMillis() + duration))
    }

    fun showRight(message: String, duration: Long = 5000, log: Boolean = true) {
        if (log) Logger.info("[Notification-Right] $message")
        notifications.add(Notification(Type.Center, message, duration, System.currentTimeMillis() + duration))
    }

    fun onRender() {
        val current = System.currentTimeMillis()
        notifications.toList().forEach {
            if (current > it.endTime) notifications.remove(it)
            else it.type.onRender(it.message, it.duration, it.endTime - current)
        }
    }

    fun clearAll() {
        notifications.clear()
    }

    sealed class Type {
        data object Center : Type() {
            override fun onRender(message: String, duration: Long, timeLeft: Long) {
                val stage = timeLeft / duration.toDouble()
                val alpha = if (stage < 0.2) stage / 0.2 else if (stage > 0.8) (1.0 - stage) / 0.2 else 1.0
                FontRendererBig.drawCenteredString(
                    message,
                    RS.centerX,
                    RS.centerY,
                    ColorRGB.WHITE.alpha((255 * alpha).toInt())
                )
            }
        }

        data object LeftBottom : Type() {
            override fun onRender(message: String, duration: Long, timeLeft: Long) {
                TODO("Not yet implemented")
            }
        }

        data object RightCenter : Type() {
            override fun onRender(message: String, duration: Long, timeLeft: Long) {
                TODO("Not yet implemented")
            }
        }

        abstract fun onRender(message: String, duration: Long, timeLeft: Long)
    }

    private data class Notification(val type: Type, val message: String, val duration: Long, val endTime: Long)

}