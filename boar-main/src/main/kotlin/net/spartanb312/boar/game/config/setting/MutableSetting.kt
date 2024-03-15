package net.spartanb312.boar.game.config.setting

abstract class MutableSetting<T : Any>(
    final override val nameString: String,
    valueIn: T,
    override var description: String = "",
    visibility: (() -> Boolean),
) : AbstractSetting<T>() {

    init {
        visibilities.add(visibility)
    }

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
            }
        }

}