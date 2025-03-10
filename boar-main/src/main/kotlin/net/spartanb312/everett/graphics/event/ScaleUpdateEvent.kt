package net.spartanb312.everett.graphics.event

import net.spartanb312.everett.utils.event.Event
import net.spartanb312.everett.utils.event.EventBus
import net.spartanb312.everett.utils.event.EventPosting

class ScaleUpdateEvent(
    val oldScale: Float,
    val newScale: Float,
    val oldScaledWidth: Int,
    val oldScaledHeight: Int,
    val newScaledWidth: Int,
    val newScaledHeight: Int
) : Event, EventPosting by Companion {
    companion object : EventBus()
}