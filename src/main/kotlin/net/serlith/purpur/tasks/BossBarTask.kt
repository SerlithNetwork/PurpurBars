package net.serlith.purpur.tasks

import net.kyori.adventure.bossbar.BossBar
import net.serlith.purpur.PurpurBars
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.UUID

@Suppress("Unused")
abstract class BossBarTask (
    private val plugin: PurpurBars,
) : BukkitRunnable() {

    private val bossbars = mutableMapOf<UUID, BossBar>()
    private var started = false

    abstract fun createBossBar(): BossBar
    abstract fun updateBossBar(bossbar: BossBar, player: Player)

    override fun run() {
        this.bossbars.entries.forEach { bar ->
            val player = Bukkit.getPlayer(bar.key) ?: return@forEach
            this.updateBossBar(bar.value, player)
        }
    }

    override fun cancel() {
        super.cancel()
        this.bossbars.keys.forEach { uuid ->
            Bukkit.getPlayer(uuid)?.let { this.removePlayer(it) }
        }
        this.bossbars.clear()
    }

    fun removePlayer(player: Player): Boolean {
        val bossbar = this.bossbars.remove(player.uniqueId) ?: return false
        player.hideBossBar(bossbar)
        return true
    }

    fun addPlayer(player: Player) {
        this.removePlayer(player)
        val bossbar = this.createBossBar()
        this.bossbars[player.uniqueId] = bossbar
        this.updateBossBar(bossbar, player)
        player.showBossBar(bossbar)
    }

    fun refreshPlayer(player: Player) {
        val bossbar = this.bossbars[player.uniqueId] ?: return
        player.showBossBar(bossbar)
    }

    fun hasPlayer(uuid: UUID) = this.bossbars.containsKey(uuid)

    fun togglePlayer(player: Player): Boolean {
        if (this.removePlayer(player)) return false
        this.addPlayer(player)
        return true
    }

    fun start() {
        this.stop()
        this.runTaskTimerAsynchronously(this.plugin, 1, 1)
        this.started = true
    }

    fun stop() {
        if (this.started) this.cancel()
    }

    companion object {

        fun startAll() {
            RamBarTask.instance(PurpurBars.self).start()
            TpsBarTask.instance(PurpurBars.self).start()
        }

        fun stopAll() {
            RamBarTask.instance(PurpurBars.self).stop()
            TpsBarTask.instance(PurpurBars.self).stop()
        }

        fun addToAll(player: Player) {
            RamBarTask.instance(PurpurBars.self).addPlayer(player)
            TpsBarTask.instance(PurpurBars.self).addPlayer(player)
        }

        fun removeFromAll(player: Player) {
            RamBarTask.instance(PurpurBars.self).removePlayer(player)
            TpsBarTask.instance(PurpurBars.self).removePlayer(player)
        }

        fun refreshAll(player: Player) {
            RamBarTask.instance(PurpurBars.self).refreshPlayer(player)
            TpsBarTask.instance(PurpurBars.self).refreshPlayer(player)
        }

    }

}