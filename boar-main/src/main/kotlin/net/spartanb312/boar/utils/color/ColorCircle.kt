package net.spartanb312.boar.utils.color

import net.spartanb312.boar.utils.math.floorToInt

class ColorCircle(val colors: Array<ColorRGB>) {

    fun getByRate(rate: Float): ColorRGB {
        var adjustedRate = rate
        while (true) {
            if (adjustedRate >= 1f) adjustedRate -= 1f
            else break
        }
        while (true) {
            if (adjustedRate < 0f) adjustedRate += 1f
            else break
        }
        return if (colors.size == 1) colors.first()
        else {
            val slice = 1f / colors.size
            val index = (adjustedRate / slice).floorToInt()
            val progress = (adjustedRate - index * slice) / slice
            val nextIndex = if (index == colors.size - 1) 0 else index + 1
            colors[index].mix(colors[nextIndex], progress)
        }
    }

    fun offset(offsetRate: Float): ColorCircle {
        val slice = 1f / colors.size
        val colorArray = colors.clone()
        for (i in colors.indices) {
            val rate = i * slice + offsetRate
            colorArray[i] = getByRate(rate)
        }
        return ColorCircle(colorArray)
    }

}