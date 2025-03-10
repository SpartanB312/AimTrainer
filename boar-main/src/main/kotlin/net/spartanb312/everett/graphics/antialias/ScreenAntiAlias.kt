package net.spartanb312.everett.graphics.antialias

import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.event.EngineLoopEvent
import net.spartanb312.everett.utils.event.ListenerOwner
import net.spartanb312.everett.utils.event.listener

class ScreenAntiAlias : ListenerOwner() {

    var targetMode = Mode.None; private set
    private val msaa by lazy { MultiSampleAA(4, RS.width, RS.height) }
    private val fxaa by lazy { FastApproximateAA(RS.width, RS.height) }
    private val none by lazy { DefaultCanvas() }
    private var currentAA: AntiAlias = none

    init {
        listener<EngineLoopEvent.Sync.Pre> {
            when (targetMode) {
                Mode.None -> currentAA = none
                Mode.FXAA -> currentAA = fxaa
                Mode.MSAA2X -> {
                    currentAA = msaa
                    msaa.refresh(level = 2)
                }

                Mode.MSAA4X -> {
                    currentAA = msaa
                    msaa.refresh(level = 4)
                }

                Mode.MSAA8X -> {
                    currentAA = msaa
                    msaa.refresh(level = 8)
                }
            }
        }
        subscribe()
    }

    fun startRendering() = currentAA.startRendering()
    fun endRendering() = currentAA.endRendering()

    fun setMode(mode: Mode) {
        targetMode = mode
    }

    enum class Mode(val modeName: String) {
        FXAA("FXAA"),
        MSAA2X("MSAA 2x"),
        MSAA4X("MSAA 4x"),
        MSAA8X("MSAA 8x"),
        None("None")
    }

}