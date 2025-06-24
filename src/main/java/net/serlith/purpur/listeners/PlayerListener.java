package net.serlith.purpur.listeners;

import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.tasks.BossBarTask;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

public class PlayerListener implements Listener, EventExecutor {

    public PlayerListener(PurpurBars plugin) {
        plugin.getServer().getPluginManager().registerEvent(PlayerJoinEvent.class, this, RootConfig.JOIN_EVENT.PRIORITY, this, plugin);
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) {
        if (!(event instanceof PlayerJoinEvent joinEvent)) return;
        Player player = joinEvent.getPlayer();
        BossBarTask.refreshAll(player);
    }

}
