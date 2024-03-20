package net.spartanb312.boar.game.config

import com.google.gson.*
import net.spartanb312.boar.game.option.impls.*
import net.spartanb312.boar.game.render.FontCacheManager
import java.io.*

object Configs {

    private val configs = mutableListOf<Configurable>()
    private val gsonPretty: Gson = GsonBuilder().setPrettyPrinting().create()

    init {
        configs.add(ControlOption)
        configs.add(CrosshairOption)
        configs.add(VideoOption)
        configs.add(AimAssistOption)
        configs.add(AccessibilityOption)
        configs.add(FontCacheManager)
    }

    fun loadConfig(path: String) {
        val map = path.jsonMap
        configs.forEach {
            map[it.name]?.asJsonObject?.let { jo -> it.readValue(jo) }
        }
    }

    fun saveConfig(path: String, syncChunks: Boolean = true) {
        val configFile = File(path)
        if (!configFile.exists()) {
            configFile.parentFile?.mkdirs()
            configFile.createNewFile()
        }
        if (syncChunks) FontCacheManager.syncChunks()
        JsonObject().apply {
            configs.forEach {
                add(it.name, it.saveValue())
            }
        }.saveToFile(configFile)
    }

    private val String.jsonMap: Map<String, JsonElement>
        get() {
            val loadJson = BufferedReader(FileReader(this))
            val map = mutableMapOf<String, JsonElement>()
            JsonParser.parseReader(loadJson).asJsonObject.entrySet().forEach {
                map[it.key] = it.value
            }
            loadJson.close()
            return map
        }

    fun JsonObject.saveToFile(file: File) {
        val saveJSon = PrintWriter(FileWriter(file))
        saveJSon.println(gsonPretty.toJson(this))
        saveJSon.close()
    }

}