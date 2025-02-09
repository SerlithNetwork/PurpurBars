package net.serlith.purpur.listeners

import net.serlith.purpur.PurpurBars
import net.serlith.purpur.tasks.BossBarTask
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerListener (
    private val plugin: PurpurBars,
) : Listener {

    init {
        this.plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        BossBarTask.refreshAll(event.player)
    }

}