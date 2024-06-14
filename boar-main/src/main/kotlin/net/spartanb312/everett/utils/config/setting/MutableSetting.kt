package net.spartanb312.everett.utils.config.setting

import net.spartanb312.everett.utils.language.MultiText

abstract class MutableSetting<T : Any>(
    final override val nameString: String,
    valueIn: T,
    override var description: String = "",
    visibility: (() -> Boolean),
) : AbstractSetting<T>() {

    init {
        visibilities.add(visibility)
    }

    final override val multiText = MultiText(nameString)
    override val displayName by multiText
    override var aliasName = nameString
    override val defaultValue = valueIn
    override var value = valueIn
        set(value) {
            if (value != field) {
                val prev = field
                val new = value
                field = new
                valueListeners.forEach { it(prev, field) }
                listeners.forEach { it() }
                updateTimer.reset()
            }
        }

}