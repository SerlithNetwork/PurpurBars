package net.serlith.purpur.configs

import net.kyori.adventure.bossbar.BossBar
import net.serlith.purpur.tasks.TpsBarTask
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.jvm.Throws

@Suppress("unused")
class MainConfigManager (
    private val plugin: JavaPlugin,
) {

    private val fileName = "settings.yml"
    private val configPath = File(this.plugin.dataFolder, this.fileName)
    private var config: YamlConfiguration = YamlConfiguration.loadConfiguration(configPath)

    init {
        this.reload()
    }

    lateinit var rambar: RamBarConfig
    class RamBarConfig (
        val title: String,
        val progressOverlay: BossBar.Overlay,
        val progressColor: ProgressColor,
        val textColor: TextColor,
        val tickInterval: Int,
    )  {
        companion object {
            @JvmStatic
            fun deserialize(section: ConfigurationSection): RamBarConfig = RamBarConfig(
                section.getString("title")!!,
                BossBar.Overlay.valueOf(section.getString("progress_overlay")!!),
                ProgressColor.deserialize(section.getConfigurationSection("progress_color")!!),
                TextColor.deserialize(section.getConfigurationSection("text_color")!!),
                section.getInt("tick_interval"),
            )
        }
    }

    lateinit var tpsbar: TpsBarConfig
    class TpsBarConfig (
        val title: String,
        val progressOverlay: BossBar.Overlay,
        val progressFillMode: TpsBarTask.FillMode,
        val progressColor: ProgressColor,
        val textColor: TextColor,
        val tickInterval: Int,
    ) {
        companion object {
            @JvmStatic
            fun deserialize(section: ConfigurationSection): TpsBarConfig = TpsBarConfig(
                section.getString("title")!!,
                BossBar.Overlay.valueOf(section.getString("progress_overlay")!!),
                TpsBarTask.FillMode.valueOf(section.getString("progress_fill_mode")!!),
                ProgressColor.deserialize(section.getConfigurationSection("progress_color")!!),
                TextColor.deserialize(section.getConfigurationSection("text_color")!!),
                section.getInt("tick_interval"),
            )
        }
    }

    class ProgressColor (
        val good: BossBar.Color,
        val medium: BossBar.Color,
        val low: BossBar.Color,
    ) {
        companion object {
            @JvmStatic
            fun deserialize(section: ConfigurationSection): ProgressColor = ProgressColor(
                BossBar.Color.valueOf(section.getString("good")!!),
                BossBar.Color.valueOf(section.getString("medium")!!),
                BossBar.Color.valueOf(section.getString("low")!!),
            )
        }
    }

    class TextColor (
        val good: String,
        val medium: String,
        val low: String,
    ) {
        companion object {
            @JvmStatic
            fun deserialize(section: ConfigurationSection): TextColor = TextColor(
                section.getString("good")!!,
                section.getString("medium")!!,
                section.getString("low")!!,
            )
        }
    }

    lateinit var messages: MessagesConfig
    class MessagesConfig (
        val noPermission: String,
        val notFound: String,
        val notPlayer: String,
        val failedReload: String,
        val successfulReload: String,
    ) {
        companion object {
            @JvmStatic
            fun deserialize(section: ConfigurationSection): MessagesConfig = MessagesConfig(
                section.getString("no_permission")!!,
                section.getString("not_found")!!,
                section.getString("not_player")!!,
                section.getString("failed_reload")!!,
                section.getString("successful_reload")!!,
            )
        }
    }

    @Throws(Exception::class)
    fun reload() {
        if (!this.configPath.exists()) {
            this.plugin.saveResource(this.fileName, false)
        }

        this.config = YamlConfiguration.loadConfiguration(this.configPath)

        val format = this.config.getConfigurationSection("format")!!
        this.rambar = RamBarConfig.deserialize(format.getConfigurationSection("rambar")!!)
        this.tpsbar = TpsBarConfig.deserialize(format.getConfigurationSection("tpsbar")!!)

        val messages = this.config.getConfigurationSection("messages")!!
        this.messages = MessagesConfig.deserialize(messages)
    }

}