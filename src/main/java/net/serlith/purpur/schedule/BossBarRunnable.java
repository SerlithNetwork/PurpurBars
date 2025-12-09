package net.serlith.purpur.schedule;

import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.configs.types.PapiBarEntry;
import net.serlith.purpur.tasks.AbstractTask;
import net.serlith.purpur.tasks.custom.PapiBarTask;
import net.serlith.purpur.tasks.region.RegionBarTask;
import net.serlith.purpur.tasks.world.WorldBarTask;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class BossBarRunnable implements Runnable {

    private static final Comparator<AbstractTask> TASK_COMPARATOR = Comparator.comparingInt(a -> RootConfig.JOIN_EVENT.ORDER.indexOf(a.getType()));
    private final List<AbstractTask> tasks = new ArrayList<>();
    private final Map<String, WorldBarTask> worldTasks = new ConcurrentHashMap<>();
    private final Map<String, RegionBarTask> regionTasks = new ConcurrentHashMap<>();
    private final Map<String, PapiBarTask> papiTasks = new ConcurrentHashMap<>();
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

    public @Nullable WorldBarTask getWorldBarTask(String worldName) {
        return worldTasks.get(worldName);
    }

    public void addWorldTask(String name, WorldBarTask task) {
        if (!this.worldTasks.containsKey(name)) {
            this.worldTasks.put(name, task);
        }
    }

    public void removeWorldTask(String name) {
        WorldBarTask task = this.worldTasks.remove(name);
        if (task != null) {
            task.stop();
        }
    }

    public @Nullable RegionBarTask getRegionBarTask(String playerName) {
        return regionTasks.get(playerName);
    }

    public void addRegionTask(String playerName, RegionBarTask task) {
        this.regionTasks.put(playerName, task);
    }

    public void removeRegionTask(String playerName) {
        RegionBarTask task = this.regionTasks.remove(playerName);
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
