package net.spartanb312.boar.utils.thread

import kotlinx.coroutines.CoroutineScope

object MainScope : CoroutineScope by newCoroutineScope(
    Runtime.getRuntime().availableProcessors(),
    "Coroutines"
)