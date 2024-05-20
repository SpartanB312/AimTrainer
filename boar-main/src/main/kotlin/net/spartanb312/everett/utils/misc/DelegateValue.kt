package net.spartanb312.everett.utils.misc

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class DelegateValue<T>(initialValue: T) : ReadWriteProperty<Any?, T> {
    private var value = initialValue
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}