package net.spartanb312.boar.graphics.event

import net.spartanb312.boar.utils.event.Event
import net.spartanb312.boar.utils.event.EventBus
import net.spartanb312.boar.utils.event.EventPosting

sealed class EngineLoopEvent : Event {

    abstract class Sync : EngineLoopEvent() {
        data object Pre : Sync(), EventPosting by EventBus()
        data object Post : Sync(), EventPosting by EventBus()
    }

    abstract class Task : EngineLoopEvent() {
        data object Pre : Sync(), EventPosting by EventBus()
        data object Post : Sync(), EventPosting by EventBus()
    }

    abstract class PollEvents : EngineLoopEvent() {
        data object Pre : Sync(), EventPosting by EventBus()
        data object Post : Sync(), EventPosting by EventBus()
    }

    abstract class Loop : EngineLoopEvent() {
        data object Pre : Sync(), EventPosting by EventBus()
        data object Post : Sync(), EventPosting by EventBus()
    }

    abstract class SwapBuffer : EngineLoopEvent() {
        data object Pre : Sync(), EventPosting by EventBus()
        data object Post : Sync(), EventPosting by EventBus()
    }

}