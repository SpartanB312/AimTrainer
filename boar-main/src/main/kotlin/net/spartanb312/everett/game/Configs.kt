package net.spartanb312.everett.game

import com.google.gson.*
import net.spartanb312.everett.game.option.impls.*
import net.spartanb312.everett.game.render.FontCacheManager
import net.spartanb312.everett.utils.config.Configurable
import java.io.*

object Configs {

    private val configs = mutableListOf<Configurable>()
    private val gsonPretty: Gson = GsonBuilder().setPrettyPrinting().create()

    object General : Configurable("General", Language) {
        val server by setting("Server", "106.55.160.170")
    }

    init {
        configs.add(General)
        configs.add(ControlOption)
        configs.add(VideoOption)
        configs.add(AudioOption)
        configs.add(CrosshairOption)
        configs.add(AimAssistOption)
        configs.add(AccessibilityOption)
        configs.add(CustomOption)
    }

    fun loadConfig(path: String) {
        val map = path.jsonMap
        configs.forEach {
            map[it.name]?.asJsonObject?.let { jo -> it.readValue(jo) }
        }
        val fontMap = (path.substringBeforeLast(".") + ".fontcfg").jsonMap
        fontMap[FontCacheManager.name]?.asJsonObject?.let { jo -> FontCacheManager.readValue(jo) }
    }

    fun saveConfig(path: String, syncChunks: Boolean = true) {
        val configFile = File(path)
        if (!configFile.exists()) {
            configFile.parentFile?.mkdirs()
            configFile.createNewFile()
        }
        val fontConfig = File(path.substringBeforeLast(".") + ".fontcfg")
        if (!fontConfig.exists()) {
            fontConfig.parentFile?.mkdirs()
            fontConfig.createNewFile()
        }
        if (syncChunks) FontCacheManager.syncChunks()
        JsonObject().apply {
            configs.forEach {
                add(it.name, it.saveValue())
            }
        }.saveToFile(configFile)
        JsonObject().apply {
            add(FontCacheManager.name, FontCacheManager.saveValue())
        }.saveToFile(fontConfig)
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