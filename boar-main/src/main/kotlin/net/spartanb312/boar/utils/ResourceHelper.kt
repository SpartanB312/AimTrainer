package net.spartanb312.boar.utils

import net.spartanb312.boar.AimTrainer
import net.spartanb312.boar.launch.LaunchClassLoader
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

object ResourceHelper {

    private val paths = mutableListOf("")

    fun getResourceStream(path: String): InputStream? {
        val path1 = if (path.startsWith("/")) path else "/$path"
        val inJar = LaunchClassLoader::class.java.getResource(path1)
        if (inJar != null) return inJar.openStream()
        for (p in paths) {
            val name = p + path.removePrefix("/")
            val file = File(name)
            if (file.exists()) {
                return FileInputStream(file)
            }
        }
        return null
    }

    fun addPath(path: String): Boolean {
        val correctedPath = if (path.endsWith("/")) path else "$path/"
        return if (!paths.contains(correctedPath)) {
            paths.add(correctedPath)
            true
        } else false
    }

    fun removePath(path: String): Boolean {
        AimTrainer::class.java.instance
        val correctedPath = if (path.endsWith("/")) path else "$path/"
        return if (paths.contains(correctedPath)) {
            paths.remove(correctedPath)
            true
        } else false
    }

}

inline val <reified T> Class<out T>.instance: T?
    get() = try {
        this.getDeclaredField("INSTANCE")[null] as T?
    } catch (ignore: Exception) {
        null
    }

@DslMarker
annotation class OpenGLDSL