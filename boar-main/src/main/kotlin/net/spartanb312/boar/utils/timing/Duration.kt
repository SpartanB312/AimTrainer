package net.spartanb312.boar.utils.timing

/**
 * Created on 2/12/2023 by B312
 */
enum class Duration(val multiplier: Int) {
    Millisecond(1),
    Second(1000),
    Minutes(60000),
    Hour(3600000),
    Day(86400000),
    Week(604800000)
}