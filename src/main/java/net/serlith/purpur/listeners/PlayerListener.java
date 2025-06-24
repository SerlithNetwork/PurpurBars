package net.serlith.purpur.listeners;

import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.data.DataStorage;
import net.serlith.purpur.tasks.BossBarTask;
import net.serlith.purpur.tasks.RamBarTask;
import net.serlith.purpur.tasks.TpsBarTask;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

public class PlayerListener implements Listener, EventExecutor {

    private final PurpurBars plugin;

    public PlayerListener(PurpurBars plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvent(PlayerJoinEvent.class, this, RootConfig.JOIN_EVENT.PRIORITY, this, plugin);
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) {
        if (!(event instanceof PlayerJoinEvent joinEvent)) return;
        Player player = joinEvent.getPlayer();
        if (RootConfig.JOIN_EVENT.SAVE_ACROSS_RESTARTS) {
            if (DataStorage.TPS_BAR.contains(player.getUniqueId())) {
                TpsBarTask.getInstance(this.plugin).lazyAddPlayer(player);
            }
            if (DataStorage.RAM_BAR.contains(player.getUniqueId())) {
                RamBarTask.getInstance(this.plugin).lazyAddPlayer(player);
            }
        }
        BossBarTask.refreshAll(player);
    }

}
