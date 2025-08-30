package net.spartanb312.everett.launch

import net.spartanb312.everett.utils.ResourceHelper
import java.awt.Graphics
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel

class LaunchScreen : JFrame("Aim Trainer") {

    init {
        val p = Splash()
        contentPane.add(p)
        setSize(800, 450)
        //val icon = ImageIcon(ResourceHelper.getResourceStream("/assets/texture/SplashScreen.png")!!.readAllBytes())
        //iconImage = icon.getImage()
        setLocationRelativeTo(null)
        isUndecorated = true
        isVisible = true
    }

    class Splash : JLabel() {
        public override fun paintComponent(g: Graphics) {
            var x = 0
            var y = 0
            val icon = ImageIcon(ResourceHelper.getResourceStream("/assets/texture/SplashScreen.png")!!.readAllBytes())
            g.drawImage(icon.getImage(), x, y, size.width, size.height, this)
            while (true) {
                g.drawImage(icon.getImage(), x, y, this)
                if (x > size.width && y > size.height) break
                if (x > size.width) {
                    x = 0
                    y += icon.iconHeight
                } else x += icon.iconWidth
            }
        }
    }

}