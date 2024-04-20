package net.spartanb312.everett.utils.misc

import java.io.File

fun createFile(path: String): File {
    return File(path).apply {
        if (!exists()) {
            parentFile?.mkdirs()
            createNewFile()
        }
    }
}