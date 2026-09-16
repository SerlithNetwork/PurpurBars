package net.serlith.purpur.tasks.world;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.WorldConfig;
import net.serlith.purpur.data.DataStorage;
import net.serlith.purpur.tasks.AbstractPerformanceTask;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@NullMarked
public class WorldFollowBarTask extends AbstractPerformanceTask {

    private static @Nullable WorldFollowBarTask INSTANCE;
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
        return BossBar.bossBar(Component.empty(), 0F, WorldConfig.getInstance().format.worldFollowBar.progressColor.good, WorldConfig.getInstance().format.worldFollowBar.progressOverlay);
    }

    @Override
    protected void updateBossBar(BossBar bossBar, Player player) {
        World world = player.getWorld();
        double mspt = world.getAverageTickTime();
        bossBar.progress(this.getPercent(mspt));
        bossBar.color(this.getBossBarColor(mspt));
        bossBar.name(MiniMessage.miniMessage().deserialize(WorldConfig.getInstance().format.worldFollowBar.title,
                Placeholder.component("mspt", this.getMsptColor(mspt)),
                Placeholder.component("world", this.getWorldColor(this.getWorldName(world), mspt)),
                Placeholder.component("ping", this.getPingColor(player.getPing()))
        ));
    }

    @Override
    public Type getType() {
        return Type.WORLD_FOLLOW_BAR;
    }

    @Override
    public void run() {
        if (++this.tick % WorldConfig.getInstance().format.worldFollowBar.updateInterval != 0) return;
        super.run();
    }

    @Override
    public void dumpAllPlayerUUIDs() {
        DataStorage.getInstance().worldFollowBar = this.getAllPlayerUUIDs();
    }

    @Override
    public Set<UUID> loadAllPlayerUUIDs() {
        return DataStorage.getInstance().worldFollowBar;
    }

    private float getPercent(double mspt) {
        return Math.clamp(((float) mspt) / 50F, 0F, 1F);
    }

    private BossBar.Color getBossBarColor(double mspt) {
        BossBar.Color color;
        if (this.isGood(mspt)) {
            color = WorldConfig.getInstance().format.worldFollowBar.progressColor.good;
        } else if (this.isMedium(mspt)) {
            color = WorldConfig.getInstance().format.worldFollowBar.progressColor.medium;
        } else {
            color = WorldConfig.getInstance().format.worldFollowBar.progressColor.low;
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
            colored = WorldConfig.getInstance().format.worldFollowBar.textColor.good;
        } else if (this.isMedium(mspt)) {
            colored = WorldConfig.getInstance().format.worldFollowBar.textColor.medium;
        } else {
            colored = WorldConfig.getInstance().format.worldFollowBar.textColor.low;
        }
        return colored;
    }

    private String getPingHealthColor(double ping) {
        String colored;
        if (ping < 100) {
            colored = WorldConfig.getInstance().format.worldFollowBar.textColor.good;
        } else if (ping < 200) {
            colored = WorldConfig.getInstance().format.worldFollowBar.textColor.medium;
        } else {
            colored = WorldConfig.getInstance().format.worldFollowBar.textColor.low;
        }
        return colored;
    }

    private boolean isGood(double mspt) {
        return mspt < 40;
    }

    private boolean isMedium(double mspt) {
        return mspt < 50;
    }

    private String getWorldName(final World world) {
        return WorldConfig.getInstance().format.worldFollowBar.useMinimalName ? world.getKey().asMinimalString() : world.getKey().asString();
    }

}
