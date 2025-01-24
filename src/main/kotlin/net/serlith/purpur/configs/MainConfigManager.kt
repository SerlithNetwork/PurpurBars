package net.serlith.purpur.configs

import net.kyori.adventure.bossbar.BossBar
import net.serlith.purpur.tasks.TpsBarTask
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * I don't like how bukkit handles configs
**/
class MainConfigManager (
    private val plugin: JavaPlugin,
) {

    private val fileName = "settings.yml"
    private val configPath = File(this.plugin.dataFolder, this.fileName)
    private var config: YamlConfiguration = YamlConfiguration.loadConfiguration(configPath)

    init {
        ConfigurationSerialization.registerClass(RamBarConfig::class.java)
        ConfigurationSerialization.registerClass(TpsBarConfig::class.java)

        this.reload()
    }

    lateinit var rambar: RamBarConfig
    class RamBarConfig (
        val title: String,
        val progressOverlay: BossBar.Overlay,
        val progressColorGood: BossBar.Color,
        val progressColorMedium: BossBar.Color,
        val progressColorLow: BossBar.Color,
        val textColorGood: String,
        val textColorMedium: String,
        val textColorLow: String,
        val tickInterval: Int,
    ) : ConfigurationSerializable {

        override fun serialize(): Map<String, Any> = mapOf(
            "title" to title,
            "progress_overlay" to progressOverlay.name,
            "progress_color_good" to progressColorGood.name,
            "progress_color_medium" to progressColorMedium.name,
            "progress_color_low" to progressColorLow.name,
            "text_color_good" to textColorGood,
            "text_color_medium" to textColorMedium,
            "text_color_low" to textColorLow,
            "tick_interval" to tickInterval,
        )

        companion object {
            fun deserialize(serialized: Map<String, Any>): RamBarConfig = RamBarConfig(
                serialized["title"] as String,
                BossBar.Overlay.valueOf(serialized["progress_overlay"] as String),
                BossBar.Color.valueOf(serialized["progress_color_good"] as String),
                BossBar.Color.valueOf(serialized["progress_color_medium"] as String),
                BossBar.Color.valueOf(serialized["progress_color_low"] as String),
                serialized["text_color_good"] as String,
                serialized["text_color_medium"] as String,
                serialized["text_color_low"] as String,
                serialized["tick_interval"] as Int,
            )
        }
    }

    lateinit var tpsbar: TpsBarConfig
    class TpsBarConfig (
        val title: String,
        val progressOverlay: BossBar.Overlay,
        val progressFillMode: TpsBarTask.FillMode,
        val progressColorGood: BossBar.Color,
        val progressColorMedium: BossBar.Color,
        val progressColorLow: BossBar.Color,
        val textColorGood: String,
        val textColorMedium: String,
        val textColorLow: String,
        val tickInterval: Int,
    ) : ConfigurationSerializable {

        override fun serialize(): Map<String, Any> = mapOf(
            "title" to title,
            "progress_overlay" to progressOverlay.name,
            "progress_fill_mode" to progressFillMode.name,
            "progress_color_good" to progressColorGood.name,
            "progress_color_medium" to progressColorMedium.name,
            "progress_color_low" to progressColorLow.name,
            "text_color_good" to textColorGood,
            "text_color_medium" to textColorMedium,
            "text_color_low" to textColorLow,
            "tick_interval" to tickInterval,
        )

        companion object {
            fun deserialize(serialized: Map<String, Any>): TpsBarConfig = TpsBarConfig(
                serialized["title"] as String,
                BossBar.Overlay.valueOf(serialized["progress_overlay"] as String),
                TpsBarTask.FillMode.valueOf(serialized["progress_fill_mode"] as String),
                BossBar.Color.valueOf(serialized["progress_color_good"] as String),
                BossBar.Color.valueOf(serialized["progress_color_medium"] as String),
                BossBar.Color.valueOf(serialized["progress_color_low"] as String),
                serialized["text_color_good"] as String,
                serialized["text_color_medium"] as String,
                serialized["text_color_low"] as String,
                serialized["tick_interval"] as Int,
            )
        }
    }

    fun reload() {
        if (!this.configPath.exists()) {
            this.plugin.saveResource(this.fileName, false)
        }

        this.config = YamlConfiguration.loadConfiguration(this.configPath)
        val main = this.config.getConfigurationSection("main")!!
        this.rambar = main.getObject("rambar", RamBarConfig::class.java)!!
        this.tpsbar = main.getObject("tpsbar", TpsBarConfig::class.java)!!

    }

}