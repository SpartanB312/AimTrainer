package net.spartanb312.boar.game.aimassist

import net.spartanb312.boar.game.Player
import net.spartanb312.boar.game.option.impls.AimAssistOption
import net.spartanb312.boar.game.render.gui.Render2DManager
import net.spartanb312.boar.game.render.scene.SceneManager
import net.spartanb312.boar.game.render.scene.impls.AimTrainingScene
import net.spartanb312.boar.game.training.QuickPlay
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.utils.math.ConvergeUtil.converge
import net.spartanb312.boar.utils.timing.Timer
import org.lwjgl.glfw.GLFW

object FrictionAA : AimAssist {

    private val aaTimer = Timer()

    override fun compensate(sensitivity: Double) = compensate(sensitivity, false)

    fun compensate(sensitivity: Double, flag: Boolean) {
        aaTimer.passedAndReset(5) {
            Player.sensK = if (Player.sensK == -1.0) sensitivity * 1000.0
            else {
                val firing = !Render2DManager.displaying
                        && (SceneManager.currentScene == AimTrainingScene || SceneManager.currentScene is QuickPlay.QuickPlayScene)
                        && GLFW.glfwGetMouseButton(RS.window, GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS
                val enableAA = flag || AimAssistOption.frEnabled
                val targetAA = when {
                    enableAA && Player.raytraced && firing -> AimAssistOption.firingCoefficient
                    enableAA && Player.raytraced && !firing -> AimAssistOption.raytraceCoefficient
                    else -> 1.0
                }
                if (targetAA == 1.0) Player.sensK.converge(sensitivity * 1000.0, 0.3)
                else Player.sensK.converge(sensitivity * targetAA * 1000.0, 0.5)
            }
        }
    }

}