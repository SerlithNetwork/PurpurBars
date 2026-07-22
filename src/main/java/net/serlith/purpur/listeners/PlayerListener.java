package net.serlith.purpur.listeners;

import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RootConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.EventExecutor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PlayerListener implements Listener, EventExecutor {

    private final PurpurBars plugin;

    public PlayerListener(PurpurBars plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvent(PlayerJoinEvent.class, this, RootConfig.JOIN_EVENT.PRIORITY, this, plugin);
    }

    @Override
    public void execute(Listener listener, Event event) {
        if (!(event instanceof PlayerJoinEvent joinEvent)) return;
        Player player = joinEvent.getPlayer();
        this.plugin.getBarsTask().refreshTasks(player);
    }

}
