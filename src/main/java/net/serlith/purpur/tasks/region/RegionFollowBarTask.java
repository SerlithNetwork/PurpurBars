package net.serlith.purpur.tasks.region;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RegionConfig;
import net.serlith.purpur.data.DataStorage;
import net.serlith.purpur.tasks.AbstractPerformanceTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@NullMarked
public class RegionFollowBarTask extends AbstractPerformanceTask {

    private static @Nullable RegionFollowBarTask INSTANCE;
    public static RegionFollowBarTask getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Folia TpsBar has not yet been initialized");
        }
        return INSTANCE;
    }

    private int tick = 0;

    public RegionFollowBarTask(PurpurBars plugin) {
        super(plugin);
        INSTANCE = this;
    }

    @Override
    protected BossBar createBossBar() {
        return BossBar.bossBar(Component.empty(), 0F, getInstance().getBossBarColor(20.0, 0.0, 0), RegionConfig.getInstance().format.regionFollowBar.progressOverlay);
    }

    @Override
    protected void updateBossBar(BossBar bossBar, Player player) {
        double tps = Bukkit.getRegionTPS(player.getLocation())[0];
        double mspt = this.plugin.isSupportsFoliaMSPT() ? Bukkit.getRegionAverageTickTimes(player.getLocation())[0] : 0.0;
        int ping = player.getPing();

        bossBar.progress(this.getPercent(tps, mspt, ping));
        bossBar.color(this.getBossBarColor(tps, mspt, ping));
        bossBar.name(MiniMessage.miniMessage().deserialize(RegionConfig.getInstance().format.regionFollowBar.title,
                Placeholder.component("tps", this.getTpsColor(tps)),
                Placeholder.component("mspt", this.getMsptColor(mspt)),
                Placeholder.component("ping", this.getPingColor(ping))
        ));
    }

    @Override
    public Type getType() {
        return Type.REGION_FOLLOW_BAR;
    }

    @Override
    public void run() {
        if (++this.tick % RegionConfig.getInstance().format.regionFollowBar.updateInterval != 0) return;
        super.run();
    }

    @Override
    public Set<UUID> loadAllPlayerUUIDs() {
        return DataStorage.getInstance().regionFollowBar;
    }

    @Override
    public void dumpAllPlayerUUIDs() {
        DataStorage.getInstance().regionFollowBar = this.getAllPlayerUUIDs();
    }

    private BossBar.Color getBossBarColor(double tps, double mspt, int ping) {
        BossBar.Color color;
        if (this.isGood(tps, mspt, ping)) {
            color = RegionConfig.getInstance().format.regionFollowBar.progressColor.good;
        } else if (this.isMedium(tps, mspt, ping)) {
            color = RegionConfig.getInstance().format.regionFollowBar.progressColor.medium;
        } else {
            color = RegionConfig.getInstance().format.regionFollowBar.progressColor.low;
        }
        return color;
    }

    private boolean isGood(double tps, double mspt, int ping) {
        return switch (RegionConfig.getInstance().format.regionFollowBar.progressFillMode) {
            case MSPT -> mspt < 40;
            case TPS -> tps >= 19;
            case PING -> ping < 100;
            default -> false;
        };
    }

    private boolean isMedium(double tps, double mspt, int ping) {
        return switch (RegionConfig.getInstance().format.regionFollowBar.progressFillMode) {
            case MSPT -> mspt < 50;
            case TPS -> tps >= 15;
            case PING -> ping < 200;
            default -> false;
        };
    }

    private Component getTpsColor(double tps) {
        return MiniMessage.miniMessage().deserialize(this.getTpsHealthColor(RegionConfig.getInstance().format.regionFollowBar.textColor, tps), Placeholder.parsed("text", "%.2f".formatted(tps)));
    }

    private Component getMsptColor(double mspt) {
        return MiniMessage.miniMessage().deserialize(this.getMsptHealthColor(RegionConfig.getInstance().format.regionFollowBar.textColor, mspt), Placeholder.parsed("text", "%.2f".formatted(mspt)));
    }

    private Component getPingColor(int ping) {
        return MiniMessage.miniMessage().deserialize(this.getPingHealthColor(RegionConfig.getInstance().format.regionFollowBar.textColor, ping), Placeholder.parsed("text", "%d".formatted(ping)));
    }

}
