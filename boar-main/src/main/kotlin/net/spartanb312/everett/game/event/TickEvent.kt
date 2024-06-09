package net.spartanb312.everett.game.event

import net.spartanb312.everett.utils.event.Event
import net.spartanb312.everett.utils.event.EventBus
import net.spartanb312.everett.utils.event.EventPosting

sealed class TickEvent : Event {
    object Pre : TickEvent(), EventPosting by EventBus()
    object Post : TickEvent(), EventPosting by EventBus()
}