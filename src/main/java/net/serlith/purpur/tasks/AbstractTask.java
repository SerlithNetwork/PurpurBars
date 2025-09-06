package net.serlith.purpur.tasks;

import net.kyori.adventure.bossbar.BossBar;
import net.serlith.purpur.PurpurBars;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractTask implements Runnable {

    protected final PurpurBars plugin;
    private final Map<UUID, BossBar> bossBars = new ConcurrentHashMap<>();

    public AbstractTask(PurpurBars plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        try {
            this.bossBars.forEach(this::executeTask);
        } catch (Exception exception) {
            this.plugin.getLogger().severe(exception.getMessage());
            Arrays.stream(exception.getStackTrace()).map(StackTraceElement::toString).forEach(plugin.getLogger()::severe);
        }
    }

    private void executeTask(UUID uuid, BossBar bossBar) {
        @Nullable Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            this.updateBossBar(bossBar, player);
        }
    }

    public void init() {
        this.loadAllPlayerUUIDs().forEach(u -> this.bossBars.put(u, this.createBossBar()));
    }

    protected abstract BossBar createBossBar();
    protected abstract void updateBossBar(BossBar bossBar, Player player);
    public abstract Type getType();
    public abstract Set<UUID> loadAllPlayerUUIDs();
    public abstract void dumpAllPlayerUUIDs();

    public Set<UUID> getAllPlayerUUIDs() {
        return bossBars.keySet();
    }

    public boolean removePlayer(@NotNull Player player) {
        @Nullable BossBar bossBar = bossBars.remove(player.getUniqueId());
        if (bossBar != null) {
            player.hideBossBar(bossBar);
            return true;
        }
        return false;
    }

    public void addPlayer(@NotNull Player player) {
        this.removePlayer(player);
        BossBar bossBar = this.createBossBar();
        this.bossBars.put(player.getUniqueId(), bossBar);
        this.updateBossBar(bossBar, player);
        player.showBossBar(bossBar);
    }

    public void refreshPlayer(@NotNull Player player) {
        @Nullable BossBar bossBar = this.bossBars.get(player.getUniqueId());
        if (bossBar != null) {
            player.showBossBar(bossBar);
        }
    }

    public void togglePlayer(@NotNull Player player) {
        if (this.removePlayer(player)) return;
        this.addPlayer(player);
    }

    protected void removeAllPlayers() {
        this.bossBars.keySet().forEach(this::removeIfPlayerExists);
    }

    private void removeIfPlayerExists(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            this.removePlayer(player);
        }
    }

    public enum Type { TPS_BAR, RAM_BAR, COMPASS_BAR, WORLD_FOLLOW_BAR, WORLD_BAR, REGION_FOLLOW_BAR, REGION_BAR }

}
