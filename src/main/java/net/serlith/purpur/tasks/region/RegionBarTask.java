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

import java.util.Set;
import java.util.UUID;

@NullMarked
public class RegionBarTask extends AbstractPerformanceTask {

    @Getter
    private double tps = 20.0;
    @Getter
    private double mspt = 20.0;
    @Getter
    private int ping = 0;
    private int tick = 0;
    private final Player player;

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
        this.mspt = this.plugin.isSupportsFoliaMSPT() ? Bukkit.getRegionAverageTickTimes(this.player.getLocation())[0] : 0.0;
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
    public Set<UUID> loadAllPlayerUUIDs() {
        return Set.of();
    }

    @Override
    public void dumpAllPlayerUUIDs() {}


    private BossBar.Color getBossBarColor() {
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

    private Component getTpsColor() {
        return MiniMessage.miniMessage().deserialize(this.getTpsHealthColor(RegionConfig.getInstance().format.regionBar.textColor, this.tps), Placeholder.parsed("text", "%.2f".formatted(this.tps)));
    }

    private Component getMsptColor() {
        return MiniMessage.miniMessage().deserialize(this.getMsptHealthColor(RegionConfig.getInstance().format.regionBar.textColor, this.mspt), Placeholder.parsed("text", "%.2f".formatted(this.mspt)));
    }

    private Component getPlayerColor() {
        return MiniMessage.miniMessage().deserialize(this.getMsptHealthColor(RegionConfig.getInstance().format.regionBar.textColor, this.mspt), Placeholder.parsed("text", this.player.getName()));
    }

    private Component getPingColor() {
        return MiniMessage.miniMessage().deserialize(this.getPingHealthColor(RegionConfig.getInstance().format.regionBar.textColor, this.ping), Placeholder.parsed("text", "%d".formatted(this.ping)));
    }

}
