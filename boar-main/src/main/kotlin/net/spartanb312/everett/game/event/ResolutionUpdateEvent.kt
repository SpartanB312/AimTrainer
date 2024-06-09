package net.spartanb312.everett.game.event

import net.spartanb312.everett.utils.event.Event
import net.spartanb312.everett.utils.event.EventBus
import net.spartanb312.everett.utils.event.EventPosting

class ResolutionUpdateEvent(
    val oldWith: Int,
    val oldHeight: Int,
    val newWidth: Int,
    val newHeight: Int
) : Event, EventPosting by Companion {
    companion object : EventBus()
}