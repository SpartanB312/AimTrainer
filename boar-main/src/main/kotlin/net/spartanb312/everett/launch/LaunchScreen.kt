package net.spartanb312.everett.launch

import net.spartanb312.everett.graphics.AnimationFlag
import net.spartanb312.everett.graphics.Easing
import net.spartanb312.everett.utils.ResourceHelper
import net.spartanb312.everett.utils.math.ConvergeUtil.converge
import java.awt.*
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel

class LaunchScreen : JFrame("Aim Trainer") {

    private val displaying = AtomicBoolean(true)
    private val flag = AnimationFlag(Easing.OUT_CUBIC, 500f).also { it.forceUpdate(0f) }

    init {
        val p = Splash()
        contentPane.add(p)
        setSize(800, 450)
        //val icon = ImageIcon(ResourceHelper.getResourceStream("/assets/texture/SplashScreen.png")!!.readAllBytes())
        //iconImage = icon.getImage()
        setLocationRelativeTo(null)
        isUndecorated = true
        isVisible = true
        Thread {
            while (displaying.get()) {
                Thread.sleep(20)
                flag.update(targetProgress)
                val nProgress = flag.get()
                if (nProgress != progress) {
                    progress = nProgress
                    repaint()
                }
            }
        }.start()
    }

    fun stop() {
        isVisible = false
        dispose()
        displaying.set(false)
    }

    fun updateSplash(progress: Float, str: String) {
        this.targetProgress = progress
        this.str = str
        repaint()
    }

    private var targetProgress = 0f
    private var progress = 0f
    private var str = "idle"

    inner class Splash : JLabel() {
        private val font = try {
            Font.createFont(
                Font.TRUETYPE_FONT,
                ResourceHelper.getResourceStream("/assets/font/Arimo-Regular.ttf")
            ).deriveFont(20f).deriveFont(Font.PLAIN)
        } catch (e: FontFormatException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        override fun paintComponent(g: Graphics) {
            var x = 0
            var y = 0
            val g2d = g as Graphics2D
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            val icon = ImageIcon(ResourceHelper.getResourceStream("/assets/texture/SplashScreen.jpg")!!.readAllBytes())
            g.drawImage(icon.getImage(), x, y, size.width, size.height, this)
            while (true) {
                g.drawImage(icon.getImage(), x, y, this)
                if (x > size.width && y > size.height) break
                if (x > size.width) {
                    x = 0
                    y += icon.iconHeight
                } else x += icon.iconWidth
            }
            val startY = (size.height * 0.96f).toInt()
            g.color = Color.ORANGE
            g.fillRect(
                0,
                startY,
                (size.width * progress).toInt(),
                size.height - startY
            )
            g.font = font
            g.color = Color.WHITE
            g.drawString(str, 0, (size.height * 0.95f).toInt())
        }
    }

}