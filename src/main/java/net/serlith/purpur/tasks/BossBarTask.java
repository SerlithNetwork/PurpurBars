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


    public Set<UUID> getAllPlayerUUIDs() {
        return bossBars.keySet();
    }

    public abstract Set<UUID> loadAllPlayerUUIDs();

    public void initializeAllPlayerUUIDs() {
        this.loadAllPlayerUUIDs().forEach(u -> this.bossBars.put(u, this.createBossBar()));
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

    public void dumpAndStop() {
        this.stop();
    }


    // Reduce lambda allocations
    private static final Comparator<BossBarTask> taskCompare = Comparator.comparingInt(a -> RootConfig.JOIN_EVENT.ORDER.indexOf(a.getType()));

    public static void startAll() {
        Stream.of(
                TpsBarTask.getInstance(PurpurBars.getInstance()),
                RamBarTask.getInstance(PurpurBars.getInstance()),
                CompassBarTask.getInstance(PurpurBars.getInstance())
        ).forEach(BossBarTask::start);
    }

    public static void dumpAndStopAll() {
        Stream.of(
                TpsBarTask.getInstance(PurpurBars.getInstance()),
                RamBarTask.getInstance(PurpurBars.getInstance()),
                CompassBarTask.getInstance(PurpurBars.getInstance())
        ).forEach(BossBarTask::dumpAndStop);
    }

    public static void loadAll() {
        Stream.of(
                TpsBarTask.getInstance(PurpurBars.getInstance()),
                RamBarTask.getInstance(PurpurBars.getInstance()),
                CompassBarTask.getInstance(PurpurBars.getInstance())
        ).forEach(BossBarTask::initializeAllPlayerUUIDs);
    }

    public static void refreshAll(Player player) {
        Stream.of(
                TpsBarTask.getInstance(PurpurBars.getInstance()),
                RamBarTask.getInstance(PurpurBars.getInstance()),
                CompassBarTask.getInstance(PurpurBars.getInstance())
        ).sorted(taskCompare).forEach(a -> a.refreshPlayer(player));
    }

    public enum Type { TPS_BAR, RAM_BAR, COMPASS_BAR }

}
