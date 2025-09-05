package net.serlith.purpur.tasks.world;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.WorldConfig;
import net.serlith.purpur.tasks.AbstractTask;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class WorldBarTask extends AbstractTask {

    private double mspt = 0.0;
    private int tick = 0;
    private final World world;

    public WorldBarTask(PurpurBars plugin, World world) {
        super(plugin);
        this.world = world;
    }

    @Override
    protected BossBar createBossBar() {
        return BossBar.bossBar(Component.empty(), 0F, this.getBossBarColor(), WorldConfig.FORMAT.WORLD_BAR.PROGRESS_OVERLAY);
    }

    @Override
    protected void updateBossBar(BossBar bossBar, Player player) {
        try { // Used here instead of AbstractTask#run for synchronization
            this.mspt = (double) this.plugin.getGetAverageTickTime().invoke(this.world);
        } catch (IllegalAccessException | InvocationTargetException ignore) {}
        bossBar.progress(this.getPercent());
        bossBar.color(this.getBossBarColor());
        bossBar.name(MiniMessage.miniMessage().deserialize(WorldConfig.FORMAT.WORLD_BAR.TITLE,
                Placeholder.component("mspt", this.getMsptColor()),
                Placeholder.component("world", this.getWorldColor())
        ));
    }

    @Override
    public Type getType() {
        return Type.WORLD_BAR;
    }

    @Override
    public void run() {
        if (++this.tick % WorldConfig.FORMAT.WORLD_BAR.UPDATE_INTERVAL != 0) return;
        super.run();
    }

    public void stop() {
        this.removeAllPlayers();
    }

    @Override
    public void dumpAllPlayerUUIDs() {
    }

    @Override
    public Set<UUID> loadAllPlayerUUIDs() {
        return Set.of();
    }

    private float getPercent() {
        return Math.max(Math.min(((float) this.mspt) / 50F, 1F), 0F);
    }

    private BossBar.Color getBossBarColor() {
        BossBar.Color color;
        if (this.isGood()) {
            color = WorldConfig.FORMAT.WORLD_BAR.PROGRESS_COLOR.GOOD;
        } else if (this.isMedium()) {
            color = WorldConfig.FORMAT.WORLD_BAR.PROGRESS_COLOR.MEDIUM;
        } else {
            color = WorldConfig.FORMAT.WORLD_BAR.PROGRESS_COLOR.LOW;
        }
        return color;
    }

    private Component getMsptColor() {
        return MiniMessage.miniMessage().deserialize(this.getColor(), Placeholder.parsed("text", "%.2f".formatted(this.mspt)));
    }

    private Component getWorldColor() {
        return MiniMessage.miniMessage().deserialize(this.getColor(), Placeholder.parsed("text", this.world.getName()));
    }

    private String getColor() {
        String colored;
        if (this.isGood()) {
            colored = WorldConfig.FORMAT.WORLD_BAR.TEXT_COLOR.GOOD;
        } else if (this.isMedium()) {
            colored = WorldConfig.FORMAT.WORLD_BAR.TEXT_COLOR.MEDIUM;
        } else {
            colored = WorldConfig.FORMAT.WORLD_BAR.TEXT_COLOR.LOW;
        }
        return colored;
    }

    private boolean isGood() {
        return this.mspt < 40;
    }

    private boolean isMedium() {
        return this.mspt < 50;
    }

}
