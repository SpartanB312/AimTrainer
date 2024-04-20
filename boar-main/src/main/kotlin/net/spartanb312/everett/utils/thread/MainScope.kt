package net.spartanb312.everett.utils.thread

import kotlinx.coroutines.CoroutineScope

object MainScope : CoroutineScope by newCoroutineScope(
    Runtime.getRuntime().availableProcessors(),
    "Coroutines"
)