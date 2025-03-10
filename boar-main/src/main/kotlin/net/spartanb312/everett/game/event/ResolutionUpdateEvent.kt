package net.spartanb312.everett.game.event

import net.spartanb312.everett.utils.event.Event
import net.spartanb312.everett.utils.event.EventBus
import net.spartanb312.everett.utils.event.EventPosting

class ResolutionUpdateEvent(
    val oldWidth: Int,
    val oldHeight: Int,
    val newWidth: Int,
    val newHeight: Int,
    val oldScaledWidth: Int,
    val oldScaledHeight: Int,
    val newScaledWidth: Int,
    val newScaledHeight: Int
) : Event, EventPosting by Companion {
    companion object : EventBus()
}