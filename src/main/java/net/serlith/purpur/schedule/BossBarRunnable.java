package net.serlith.purpur.schedule;

import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.tasks.AbstractTask;
import net.serlith.purpur.tasks.world.WorldBarTask;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class BossBarRunnable implements Runnable {

    private static final Comparator<AbstractTask> TASK_COMPARATOR = Comparator.comparingInt(a -> RootConfig.JOIN_EVENT.ORDER.indexOf(a.getType()));
    private final List<AbstractTask> tasks = new ArrayList<>();
    private final Map<String, WorldBarTask> worldTasks = new HashMap<>();

    @Override
    public void run() {
        this.tasks.forEach(AbstractTask::run);
        this.worldTasks.values().forEach(WorldBarTask::run);
    }

    public void init() {
        Stream.concat(this.tasks.stream(), this.worldTasks.values().stream())
                .sorted(TASK_COMPARATOR).forEach(AbstractTask::init);
    }

    public void stop() {
        this.tasks.forEach(AbstractTask::dumpAllPlayerUUIDs);
        this.worldTasks.values().forEach(WorldBarTask::dumpAllPlayerUUIDs);
    }

    public void addTask(AbstractTask task) {
        this.tasks.add(task);
    }

    public @Nullable WorldBarTask getWorldBarTask(String worldName) {
        return worldTasks.get(worldName);
    }

    public void addWorldTask(String name, WorldBarTask task) {
        this.worldTasks.put(name, task);
    }

    public void removeWorldTask(String name) {
        WorldBarTask task = this.worldTasks.remove(name);
        if (task != null) {
            task.stop();
        }
    }

    public void refreshTasks(Player player) {
        Stream.concat(this.tasks.stream(), this.worldTasks.values().stream())
                .sorted(TASK_COMPARATOR).forEach(i -> i.refreshPlayer(player));
    }

}
