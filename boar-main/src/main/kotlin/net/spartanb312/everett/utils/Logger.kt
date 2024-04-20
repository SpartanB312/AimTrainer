package net.spartanb312.everett.utils

import net.spartanb312.everett.graphics.RenderSystem
import net.spartanb312.everett.utils.logging.ILogger
import net.spartanb312.everett.utils.logging.SimpleLogger
import java.text.SimpleDateFormat
import java.util.*

object Logger : ILogger by SimpleLogger(
    "Boar",
    "logs/${SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(Date())}.txt",
    { RenderSystem.debugInfo }
)