package net.serlith.purpur.listeners;

import net.serlith.purpur.PurpurBars;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerRegionListener implements Listener {

    private final PurpurBars plugin;

    public PlayerRegionListener(PurpurBars plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.plugin.getBarsTask().removeRegionTask(event.getPlayer().getName());
    }

}
