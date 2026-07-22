package net.serlith.purpur.listeners;

import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.tasks.world.WorldBarTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldListener implements Listener {

    private final PurpurBars plugin;

    public WorldListener(PurpurBars plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        this.plugin.getBarsTask().addWorldTask(event.getWorld(), new WorldBarTask(this.plugin, event.getWorld()));
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        this.plugin.getBarsTask().removeWorldTask(event.getWorld());
    }

}
