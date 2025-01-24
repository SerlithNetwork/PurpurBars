package net.serlith.purpur.tasks

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.serlith.purpur.PurpurBars
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.math.max
import kotlin.math.min

class TpsBarTask (
    private val plugin: PurpurBars,
) : BossBarTask(plugin) {

    var tps = 20.0
        private set
    var mspt = 0.0
        private set
    var tick = 0
        private set

    companion object {
        private var instance: TpsBarTask? = null
        fun instance(plugin: PurpurBars): TpsBarTask {
            if (instance == null) {
                instance = TpsBarTask(plugin)
            }
            return instance!!
        }
    }

    override fun createBossBar(): BossBar =
        BossBar.bossBar(Component.text(""), 0f, instance(this.plugin).getBossBarColor(), this.plugin.mainConfigManager.tpsbar.progressOverlay)

    override fun updateBossBar(bossbar: BossBar, player: Player) {
        bossbar.progress(this.percent)
        bossbar.color(this.getBossBarColor())
        bossbar.name(MiniMessage.miniMessage().deserialize(this.plugin.mainConfigManager.tpsbar.title,
            Placeholder.component("tps", this.getTpsColor()),
            Placeholder.component("mspt", this.getMsptColor()),
            Placeholder.component("ping", this.getPingColor(player.ping)),
        ))
    }

    override fun run() {
        if (++this.tick < this.plugin.mainConfigManager.tpsbar.tickInterval) return
        this.tick = 0

        this.tps = max(min(Bukkit.getTPS()[0], 20.0), 0.0)
        this.mspt = Bukkit.getAverageTickTime()

        super.run()
    }

    val percent: Float
        get() = if (this.plugin.mainConfigManager.tpsbar.progressFillMode == FillMode.MSPT) max(min(this.mspt.toFloat() / 50f, 1f), 0f)
        else max(min(this.tps.toFloat() / 20f, 1f), 0f)

    fun getBossBarColor(): BossBar.Color =
        if (this.isGood(this.plugin.mainConfigManager.tpsbar.progressFillMode)) this.plugin.mainConfigManager.tpsbar.progressColorGood
        else if (this.isMedium(this.plugin.mainConfigManager.tpsbar.progressFillMode)) this.plugin.mainConfigManager.tpsbar.progressColorMedium
        else this.plugin.mainConfigManager.tpsbar.progressColorLow


    private fun isGood(mode: FillMode, ping: Int = 0) =
        when (mode) {
            FillMode.MSPT -> this.mspt < 40
            FillMode.TPS -> this.tps >= 19
            FillMode.PING -> ping < 100
        }

    private fun isMedium(mode: FillMode, ping: Int = 0) =
        when (mode) {
            FillMode.MSPT -> this.mspt < 50
            FillMode.TPS -> this.tps >= 15
            FillMode.PING -> ping < 200
        }

    private fun getTpsColor(): Component {
        val color = if (this.isGood(FillMode.TPS)) this.plugin.mainConfigManager.tpsbar.textColorGood
        else if (this.isMedium(FillMode.TPS)) this.plugin.mainConfigManager.tpsbar.textColorMedium
        else this.plugin.mainConfigManager.tpsbar.textColorLow

        return MiniMessage.miniMessage().deserialize(color, Placeholder.parsed("text", "%.2f".format(this.tps)))
    }

    private fun getMsptColor(): Component {
        val color = if (this.isGood(FillMode.MSPT)) this.plugin.mainConfigManager.tpsbar.textColorGood
        else if (this.isMedium(FillMode.MSPT)) this.plugin.mainConfigManager.tpsbar.textColorMedium
        else this.plugin.mainConfigManager.tpsbar.textColorLow

        return MiniMessage.miniMessage().deserialize(color, Placeholder.parsed("text", "%.2f".format(this.mspt)))
    }

    private fun getPingColor(ping: Int): Component {
        val color = if (this.isGood(FillMode.PING, ping)) this.plugin.mainConfigManager.tpsbar.textColorGood
        else if (this.isMedium(FillMode.PING, ping)) this.plugin.mainConfigManager.tpsbar.textColorMedium
        else this.plugin.mainConfigManager.tpsbar.textColorLow

        return MiniMessage.miniMessage().deserialize(color, Placeholder.parsed("text", "$ping"))
    }

    enum class FillMode {
        TPS, MSPT, PING
    }

}