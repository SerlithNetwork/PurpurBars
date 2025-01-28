package net.serlith.purpur.tasks

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.serlith.purpur.PurpurBars
import org.bukkit.entity.Player
import java.lang.management.ManagementFactory
import kotlin.math.max
import kotlin.math.min

class RamBarTask (
    private val plugin: PurpurBars,
) : BossBarTask(plugin) {

    var allocated = 0L
        private set
    var used = 0L
        private set
    var xmx = 0L
        private set
    var xms = 0L
        private set
    var percent = 0f
        private set
    var tick = 0
        private set

    companion object {
        private var instance: RamBarTask? = null
        fun instance(plugin: PurpurBars): RamBarTask {
            if (instance == null) {
                instance = RamBarTask(plugin)
            }
            return instance!!
        }
    }

    override fun createBossBar(): BossBar =
        BossBar.bossBar(Component.text(""), 0f, instance(this.plugin).getBossBarColor(), this.plugin.mainConfigManager.rambar.progressOverlay)

    override fun updateBossBar(bossbar: BossBar, player: Player) {
        bossbar.progress(this.percent)
        bossbar.color(this.getBossBarColor())
        bossbar.name(MiniMessage.miniMessage().deserialize(this.plugin.mainConfigManager.rambar.title,
            Placeholder.component("allocated", format(this.allocated)),
            Placeholder.component("used", format(this.used)),
            Placeholder.component("xmx", format(this.xmx)),
            Placeholder.component("xms", format(this.xms)),
            Placeholder.unparsed("percent", "${(this.percent * 100).toInt()}%")
        ))
    }

    override fun run() {
        if (++this.tick < this.plugin.mainConfigManager.rambar.tickInterval) return
        this.tick = 0

        val heap = ManagementFactory.getMemoryMXBean().heapMemoryUsage

        this.allocated = heap.committed
        this.used = heap.used
        this.xmx = heap.max
        this.xms = heap.init
        this.percent = max(min((this.used.toFloat() / this.xmx), 1f), 0f)

        super.run()
    }

    private fun getBossBarColor(): BossBar.Color =
        if (this.percent < 0.5f) this.plugin.mainConfigManager.rambar.progressColor.good
        else if (this.percent < 0.75f) this.plugin.mainConfigManager.rambar.progressColor.medium
        else this.plugin.mainConfigManager.rambar.progressColor.low

    fun format(v: Long): Component {
        val color = if (this.percent < 0.60f) this.plugin.mainConfigManager.rambar.textColor.good
        else if (this.percent < 0.85f) this.plugin.mainConfigManager.rambar.textColor.medium
        else this.plugin.mainConfigManager.rambar.textColor.low

        val value = if (v < 1024) "${v}B"
        else {
            val z = (63 - v.countLeadingZeroBits()) / 10
            if (z > 2) "%.1f".format(v.toFloat() / (1L shl (z * 10))) + "${ "BKMGTPE"[z] }"
            else "${ v / (1L shl (z * 10)) }${ "BKMGTPE"[z] }"
        }

        return MiniMessage.miniMessage().deserialize(color, Placeholder.unparsed("text", value))
    }

}