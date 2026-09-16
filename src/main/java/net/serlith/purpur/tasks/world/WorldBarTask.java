package net.serlith.purpur.tasks.world;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.WorldConfig;
import net.serlith.purpur.tasks.AbstractPerformanceTask;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.*;

@NullMarked
public class WorldBarTask extends AbstractPerformanceTask {

    @Getter
    private double mspt = 0.0;
    private int tick = 0;
    private final World world;

    public WorldBarTask(PurpurBars plugin, World world) {
        super(plugin);
        this.world = world;
    }

    @Override
    protected BossBar createBossBar() {
        return BossBar.bossBar(Component.empty(), 0F, this.getBossBarColor(), WorldConfig.getInstance().format.worldBar.progressOverlay);
    }

    @Override
    protected void updateBossBar(BossBar bossBar, Player player) {
        this.mspt = this.world.getAverageTickTime();
        bossBar.progress(this.getPercent());
        bossBar.color(this.getBossBarColor());
        bossBar.name(MiniMessage.miniMessage().deserialize(WorldConfig.getInstance().format.worldBar.title,
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
        if (++this.tick % WorldConfig.getInstance().format.worldBar.updateInterval != 0) return;
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
        return Math.clamp(((float) this.mspt) / 50F, 0F, 1F);
    }

    @Override
    protected boolean isGood(final int ping) {
        return this.mspt < 40;
    }

    @Override
    protected boolean isMedium(final int ping) {
        return this.mspt < 50;
    }

    private BossBar.Color getBossBarColor() {
        return this.getBossBarColor(WorldConfig.getInstance().format.worldBar.progressColor, 0);
    }

    private Component getMsptColor() {
        return MiniMessage.miniMessage().deserialize(this.getColor(), Placeholder.parsed("text", "%.2f".formatted(this.mspt)));
    }

    private Component getWorldColor() {
        return MiniMessage.miniMessage().deserialize(this.getColor(), Placeholder.parsed("text", this.getWorldName(this.world)));
    }

    private String getColor() {
        return this.getMsptHealthColor(WorldConfig.getInstance().format.worldBar.textColor, this.mspt);
    }

    private String getWorldName(final World world) {
        return WorldConfig.getInstance().format.worldBar.useMinimalName ? world.getKey().asMinimalString() : world.getKey().asString();
    }

}
