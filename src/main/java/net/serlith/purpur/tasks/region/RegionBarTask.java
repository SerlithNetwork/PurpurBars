package net.serlith.purpur.tasks.region;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RegionConfig;
import net.serlith.purpur.tasks.AbstractPerformanceTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.UUID;

@NullMarked
public class RegionBarTask extends AbstractPerformanceTask {

    @Getter
    protected double tps = 20.0;
    @Getter
    protected double mspt = 20.0;
    @Getter
    protected int ping = 0;

    protected final Player player;
    private int tick = 0;

    public RegionBarTask(PurpurBars plugin, Player player) {
        super(plugin);
        this.player = player;
    }

    @Override
    protected BossBar createBossBar() {
        return BossBar.bossBar(Component.empty(), 0F, this.getBossBarColor(), RegionConfig.getInstance().format.regionBar.progressOverlay);
    }

    @Override
    protected void updateBossBar(BossBar bossBar, Player player) {
        this.tps = Bukkit.getRegionTPS(this.player.getLocation())[0];
        this.mspt = Bukkit.getRegionAverageTickTimes(this.player.getLocation())[0];
        this.ping = this.player.getPing();

        bossBar.progress(this.getPercent(this.tps, this.mspt, this.ping));
        bossBar.color(this.getBossBarColor());
        bossBar.name(MiniMessage.miniMessage().deserialize(RegionConfig.getInstance().format.regionBar.title,
                Placeholder.component("tps", this.getTpsColor()),
                Placeholder.component("mspt", this.getMsptColor()),
                Placeholder.component("player", this.getPlayerColor()),
                Placeholder.component("ping", this.getPingColor())
        ));
    }

    @Override
    public Type getType() {
        return Type.REGION_BAR;
    }

    @Override
    public void run() {
        if (++this.tick % RegionConfig.getInstance().format.regionBar.updateInterval != 0) return;
        super.run();
    }

    public void stop() {
        this.removeAllPlayers();
    }

    @Override
    public List<UUID> loadAllPlayerUUIDs() {
        return List.of();
    }

    @Override
    public void dumpAllPlayerUUIDs() {}


    protected BossBar.Color getBossBarColor() {
        BossBar.Color color;
        if (this.isGood()) {
            color = RegionConfig.getInstance().format.regionBar.progressColor.good;
        } else if (this.isMedium()) {
            color = RegionConfig.getInstance().format.regionBar.progressColor.medium;
        } else {
            color = RegionConfig.getInstance().format.regionBar.progressColor.low;
        }
        return color;
    }

    private boolean isGood() {
        return switch (RegionConfig.getInstance().format.regionBar.progressFillMode) {
            case MSPT -> mspt < 40;
            case TPS -> tps >= 19;
            case PING -> ping < 100;
            default -> false;
        };
    }

    private boolean isMedium() {
        return switch (RegionConfig.getInstance().format.regionBar.progressFillMode) {
            case MSPT -> mspt < 50;
            case TPS -> tps >= 15;
            case PING -> ping < 200;
            default -> false;
        };
    }

    protected Component getTpsColor() {
        return MiniMessage.miniMessage().deserialize(this.getTpsHealthColor(RegionConfig.getInstance().format.regionBar.textColor, this.tps), Placeholder.parsed("text", "%.2f".formatted(this.tps)));
    }

    protected Component getMsptColor() {
        return MiniMessage.miniMessage().deserialize(this.getMsptHealthColor(RegionConfig.getInstance().format.regionBar.textColor, this.mspt), Placeholder.parsed("text", "%.2f".formatted(this.mspt)));
    }

    protected Component getPlayerColor() {
        return MiniMessage.miniMessage().deserialize(this.getMsptHealthColor(RegionConfig.getInstance().format.regionBar.textColor, this.mspt), Placeholder.parsed("text", this.player.getName()));
    }

    protected Component getPingColor() {
        return MiniMessage.miniMessage().deserialize(this.getPingHealthColor(RegionConfig.getInstance().format.regionBar.textColor, this.ping), Placeholder.parsed("text", "%d".formatted(this.ping)));
    }

}
