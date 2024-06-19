package net.spartanb312.everett.game

import com.google.gson.*
import net.spartanb312.everett.game.option.impls.*
import net.spartanb312.everett.game.render.FontCacheManager
import net.spartanb312.everett.game.training.custom.CustomTraining
import net.spartanb312.everett.utils.config.IConfigurable
import java.io.*

object Configs {

    private val configs = mutableListOf<IConfigurable<*>>()
    private val customConfigs = mutableListOf<IConfigurable<*>>()
    private val gsonPretty: Gson = GsonBuilder().setPrettyPrinting().create()

    init {
        configs.add(ControlOption)
        configs.add(VideoOption)
        configs.add(AudioOption)
        configs.add(CrosshairOption)
        configs.add(AimAssistOption)
        configs.add(AccessibilityOption)

        customConfigs.add(CustomTraining)
        CustomTraining.Modes.entries.forEach {
            customConfigs.add(it.settingGroup)
        }
    }

    fun loadConfig(path: String) {
        // General configs
        val generalMap = path.jsonMap
        configs.forEach {
            generalMap[it.name]?.asJsonObject?.let { jo -> it.readValue(jo) }
        }

        // Custom game configs
        val customMap = (path.substringBeforeLast(".") + ".customcfg").jsonMap
        customConfigs.forEach {
            customMap[it.name]?.asJsonObject?.let { jo -> it.readValue(jo) }
        }

        // Font config
        val fontMap = (path.substringBeforeLast(".") + ".fontcfg").jsonMap
        fontMap[FontCacheManager.name]?.asJsonObject?.let { jo -> FontCacheManager.readValue(jo) }
    }

    fun saveConfig(path: String, syncChunks: Boolean = true) {
        if (syncChunks) FontCacheManager.syncChunks()

        // General configs
        val configFile = File(path)
        if (!configFile.exists()) {
            configFile.parentFile?.mkdirs()
            configFile.createNewFile()
        }
        JsonObject().apply {
            configs.forEach {
                add(it.name, it.saveValue())
            }
        }.saveToFile(configFile)

        // Custom game config
        val customConfigFile = File(path.substringBeforeLast(".") + ".customcfg")
        if (!customConfigFile.exists()) {
            customConfigFile.parentFile?.mkdirs()
            customConfigFile.createNewFile()
        }
        JsonObject().apply {
            customConfigs.forEach {
                add(it.name, it.saveValue())
            }
        }.saveToFile(customConfigFile)

        // Font config
        val fontConfig = File(path.substringBeforeLast(".") + ".fontcfg")
        if (!fontConfig.exists()) {
            fontConfig.parentFile?.mkdirs()
            fontConfig.createNewFile()
        }
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