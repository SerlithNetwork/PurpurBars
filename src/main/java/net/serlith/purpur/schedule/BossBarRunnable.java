package net.serlith.purpur.schedule;

import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.configs.types.PapiBarEntry;
import net.serlith.purpur.tasks.AbstractTask;
import net.serlith.purpur.tasks.custom.PapiBarTask;
import net.serlith.purpur.tasks.region.RegionBarTask;
import net.serlith.purpur.tasks.world.WorldBarTask;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

@NullMarked
public class BossBarRunnable implements Runnable {

    private static final Comparator<AbstractTask> TASK_COMPARATOR = Comparator.comparingInt(a -> RootConfig.JOIN_EVENT.ORDER.indexOf(a.getType()));
    private final List<AbstractTask> tasks = new ArrayList<>();
    private final ConcurrentMap<NamespacedKey, WorldBarTask> worldTasks = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, RegionBarTask> regionTasks = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, PapiBarTask> papiTasks = new ConcurrentHashMap<>();
    private final PurpurBars plugin;

    public BossBarRunnable(PurpurBars plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.tasks.forEach(AbstractTask::run);
        this.worldTasks.values().forEach(WorldBarTask::run);
        this.regionTasks.values().forEach(RegionBarTask::run);
        this.papiTasks.values().forEach(PapiBarTask::run);
    }

    public void init() {
        Stream.of(this.tasks,
                        this.worldTasks.values(),
                        this.regionTasks.values(),
                        this.papiTasks.values()
                )
                .flatMap(Collection::stream)
                .sorted(TASK_COMPARATOR)
                .forEach(AbstractTask::init);
    }

    public void stop() {
        Stream.of(this.tasks,
                        this.worldTasks.values(),
                        this.regionTasks.values(),
                        this.papiTasks.values()
                )
                .flatMap(Collection::stream)
                .forEach(AbstractTask::dumpAllPlayerUUIDs);
    }

    public void addTask(AbstractTask task) {
        this.tasks.add(task);
    }

    public @Nullable WorldBarTask getWorldBarTask(World world) {
        return this.worldTasks.get(world.getKey());
    }

    public @Nullable WorldBarTask getWorldBarTask(NamespacedKey key) {
        return this.worldTasks.get(key);
    }

    public void addWorldTask(World world, WorldBarTask task) {
        this.worldTasks.putIfAbsent(world.getKey(), task);
    }

    public void removeWorldTask(World world) {
        WorldBarTask task = this.worldTasks.remove(world.getKey());
        if (task != null) {
            task.stop();
        }
    }

    public @Nullable RegionBarTask getRegionBarTask(Player player) {
        return this.regionTasks.get(player.getUniqueId());
    }

    public void addRegionTask(Player player, RegionBarTask task) {
        this.regionTasks.put(player.getUniqueId(), task);
    }

    public void removeRegionTask(Player player) {
        RegionBarTask task = this.regionTasks.remove(player.getUniqueId());
        if (task != null) {
            task.stop();
        }
    }

    public @Nullable PapiBarTask getPapiBarTask(String name) {
        return papiTasks.get(name);
    }

    public void addNotPresentPapiBarTasks(Collection<PapiBarEntry> entries) {
        for (PapiBarEntry entry : entries) {
            this.papiTasks.putIfAbsent(entry.name(), new PapiBarTask(this.plugin, entry));
        }
    }

    public void updatePresentPapiBarTasks(Collection<PapiBarEntry> entries) {
        for (PapiBarEntry entry : entries) {
            PapiBarTask task = this.papiTasks.get(entry.name());
            if (task != null) {
                task.setEntry(entry);
            }
        }
    }

    public void removeNotPresentPapiBarTasks(Collection<String> names) {
        Iterator<String> keys = this.papiTasks.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            if (!names.contains(key)) {
                PapiBarTask task = this.papiTasks.get(key);
                if (task != null) {
                    task.stop();
                }
                keys.remove();
            }
        }
    }

    public void refreshTasks(Player player) {
        Stream.of(this.tasks,
                        this.worldTasks.values(),
                        this.regionTasks.values(),
                        this.papiTasks.values()
                )
                .flatMap(Collection::stream)
                .sorted(TASK_COMPARATOR)
                .forEach(i -> i.refreshPlayer(player));
    }

}
