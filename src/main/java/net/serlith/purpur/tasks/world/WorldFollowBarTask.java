package net.serlith.purpur.tasks.world;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.WorldConfig;
import net.serlith.purpur.data.DataStorage;
import net.serlith.purpur.tasks.AbstractTask;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.UUID;

public class WorldFollowBarTask extends AbstractTask {

    private static WorldFollowBarTask INSTANCE;
    public static WorldFollowBarTask getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("WorldFollowBar has not yet been initialized");
        }
        return INSTANCE;
    }

    @Getter
    private int tick = 0;

    public WorldFollowBarTask(PurpurBars plugin) {
        super(plugin);
        INSTANCE = this;
    }

    @Override
    protected BossBar createBossBar() {
        return BossBar.bossBar(Component.empty(), 0F, WorldConfig.FORMAT.WORLD_FOLLOW_BAR.PROGRESS_COLOR.GOOD, WorldConfig.FORMAT.WORLD_FOLLOW_BAR.PROGRESS_OVERLAY);
    }

    @Override
    protected void updateBossBar(BossBar bossBar, Player player) {
        World world = player.getWorld();
        double mspt = 0.0;
        try {
            mspt = (double) this.plugin.getGetWorldAverageTickTime().invoke(world);
        } catch (IllegalAccessException | InvocationTargetException ignore) {}
        bossBar.progress(this.getPercent(mspt));
        bossBar.color(this.getBossBarColor(mspt));
        bossBar.name(MiniMessage.miniMessage().deserialize(WorldConfig.FORMAT.WORLD_FOLLOW_BAR.TITLE,
                Placeholder.component("mspt", this.getMsptColor(mspt)),
                Placeholder.component("world", this.getWorldColor(world.getName(), mspt)),
                Placeholder.component("ping", this.getPingColor(player.getPing()))
        ));
    }

    @Override
    public Type getType() {
        return Type.WORLD_FOLLOW_BAR;
    }

    @Override
    public void run() {
        if (++this.tick % WorldConfig.FORMAT.WORLD_FOLLOW_BAR.UPDATE_INTERVAL != 0) return;
        super.run();
    }

    @Override
    public void dumpAllPlayerUUIDs() {
        DataStorage.WORLD_FOLLOW_BAR = this.getAllPlayerUUIDs();
    }

    @Override
    public Set<UUID> loadAllPlayerUUIDs() {
        return DataStorage.WORLD_FOLLOW_BAR;
    }

    private float getPercent(double mspt) {
        return Math.max(Math.min(((float) mspt) / 50F, 1F), 0F);
    }

    private BossBar.Color getBossBarColor(double mspt) {
        BossBar.Color color;
        if (this.isGood(mspt)) {
            color = WorldConfig.FORMAT.WORLD_FOLLOW_BAR.PROGRESS_COLOR.GOOD;
        } else if (this.isMedium(mspt)) {
            color = WorldConfig.FORMAT.WORLD_FOLLOW_BAR.PROGRESS_COLOR.MEDIUM;
        } else {
            color = WorldConfig.FORMAT.WORLD_FOLLOW_BAR.PROGRESS_COLOR.LOW;
        }
        return color;
    }

    private Component getMsptColor(double mspt) {
        return MiniMessage.miniMessage().deserialize(this.getColor(mspt), Placeholder.parsed("text", "%.2f".formatted(mspt)));
    }

    private Component getPingColor(int ping) {
        return MiniMessage.miniMessage().deserialize(this.getPingHealthColor(ping), Placeholder.parsed("text", "%d".formatted(ping)));
    }

    private Component getWorldColor(String worldName, double mspt) {
        return MiniMessage.miniMessage().deserialize(this.getColor(mspt), Placeholder.parsed("text", worldName));
    }

    private String getColor(double mspt) {
        String colored;
        if (this.isGood(mspt)) {
            colored = WorldConfig.FORMAT.WORLD_FOLLOW_BAR.TEXT_COLOR.GOOD;
        } else if (this.isMedium(mspt)) {
            colored = WorldConfig.FORMAT.WORLD_FOLLOW_BAR.TEXT_COLOR.MEDIUM;
        } else {
            colored = WorldConfig.FORMAT.WORLD_FOLLOW_BAR.TEXT_COLOR.LOW;
        }
        return colored;
    }

    private String getPingHealthColor(double ping) {
        String colored;
        if (ping < 100) {
            colored = WorldConfig.FORMAT.WORLD_FOLLOW_BAR.TEXT_COLOR.GOOD;
        } else if (ping < 200) {
            colored = WorldConfig.FORMAT.WORLD_FOLLOW_BAR.TEXT_COLOR.MEDIUM;
        } else {
            colored = WorldConfig.FORMAT.WORLD_FOLLOW_BAR.TEXT_COLOR.LOW;
        }
        return colored;
    }

    private boolean isGood(double mspt) {
        return mspt < 40;
    }

    private boolean isMedium(double mspt) {
        return mspt < 50;
    }

}
