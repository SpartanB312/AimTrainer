package net.spartanb312.everett.utils.timing

/**
 * Created on 2/12/2023 by B312
 */
enum class Duration(val multiplier: Long) {
    Nanosecond(1),
    Microsecond(1000),
    Millisecond(1000000),
    Second(1000000000),
    Minute(60000000000),
    Hour(3600000000000),
    Day(86400000000000),
    Week(604800000000000)
}