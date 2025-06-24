package net.serlith.purpur.tasks;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.bossbar.BossBar;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RootConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

@RequiredArgsConstructor
public abstract class BossBarTask extends BukkitRunnable {

    private final PurpurBars plugin;

    private final Map<UUID, BossBar> bossBars = new HashMap<>();
    private boolean started = false;

    abstract BossBar createBossBar();
    abstract void updateBossBar(BossBar bossBar, Player player);
    abstract Type getType();

    // Reduce lambda allocations
    private final BiConsumer<UUID, BossBar> taskRun = (uuid, bossBar) -> {
        @Nullable Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            this.updateBossBar(bossBar, player);
        }
    };

    @Override
    public void run() {
        this.bossBars.forEach(taskRun);
    }

    @Override
    public void cancel() {
        super.cancel();
        this.bossBars.forEach((uuid, bossBar) -> {});
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

    public void lazyAddPlayer(@NotNull Player player) {
        if (!this.bossBars.containsKey(player.getUniqueId())) {
            BossBar bossBar = this.createBossBar();
            this.bossBars.put(player.getUniqueId(), bossBar);
            this.updateBossBar(bossBar, player);
        }
    }

    public void refreshPlayer(@NotNull Player player) {
        @Nullable BossBar bossBar = this.bossBars.get(player.getUniqueId());
        if (bossBar != null) {
            player.showBossBar(bossBar);
        }
    }

    public boolean hasPlayer(@NotNull Player player) {
        return this.bossBars.containsKey(player.getUniqueId());
    }

    public boolean togglePlayer(@NotNull Player player) {
        if (this.removePlayer(player)) return false;
        this.addPlayer(player);
        return true;
    }

    public void start() {
        this.stop();
        this.runTaskTimerAsynchronously(this.plugin, 1L, 1L);
        this.started = true;
    }

    public void stop() {
        if (this.started) {
            this.cancel();
        }
    }


    private static final Comparator<BossBarTask> taskCompare = Comparator.comparingInt(a -> RootConfig.JOIN_EVENT.ORDER.indexOf(a.getType()));

    public static void startAll() {
        Stream.of(
                TpsBarTask.getInstance(PurpurBars.getInstance()),
                RamBarTask.getInstance(PurpurBars.getInstance())
                )
                .sorted(taskCompare)
                .forEach(BossBarTask::start);
    }

    public static void stopAll() {
        Stream.of(
                        TpsBarTask.getInstance(PurpurBars.getInstance()),
                        RamBarTask.getInstance(PurpurBars.getInstance())
                )
                .sorted(taskCompare)
                .forEach(BossBarTask::stop);
    }

    public static void addToAll(Player player) {
        Stream.of(
                        TpsBarTask.getInstance(PurpurBars.getInstance()),
                        RamBarTask.getInstance(PurpurBars.getInstance())
                )
                .sorted(taskCompare)
                .forEach(a -> a.addPlayer(player));
    }

    public static void removeFromAll(Player player) {
        Stream.of(
                        TpsBarTask.getInstance(PurpurBars.getInstance()),
                        RamBarTask.getInstance(PurpurBars.getInstance())
                )
                .sorted(taskCompare)
                .forEach(a -> a.removePlayer(player));
    }

    public static void refreshAll(Player player) {
        Stream.of(
                        TpsBarTask.getInstance(PurpurBars.getInstance()),
                        RamBarTask.getInstance(PurpurBars.getInstance())
                )
                .sorted(taskCompare)
                .forEach(a -> a.refreshPlayer(player));
    }

    public enum Type { TPS_BAR, RAM_BAR }

}
