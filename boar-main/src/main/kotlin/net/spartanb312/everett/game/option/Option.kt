package net.spartanb312.everett.game.option

import net.spartanb312.everett.game.Language
import net.spartanb312.everett.utils.config.Configurable
import net.spartanb312.everett.utils.event.IListenerOwner
import net.spartanb312.everett.utils.event.Listener
import net.spartanb312.everett.utils.event.ParallelListener

open class Option(name: String) : Configurable(name, Language), IListenerOwner {
    override val listeners = ArrayList<Listener>()
    override val parallelListeners = ArrayList<ParallelListener>()
}