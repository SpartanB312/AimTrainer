package net.spartanb312.everett.game.network

import kotlinx.coroutines.CoroutineScope
import net.spartanb312.everett.utils.thread.ConcurrentTaskManager
import net.spartanb312.everett.utils.thread.newCoroutineScope

object NetworkingScope : CoroutineScope by newCoroutineScope(Runtime.getRuntime().availableProcessors(), "Networking")

val NetworkingWorkers = ConcurrentTaskManager("Networking", scope = NetworkingScope)