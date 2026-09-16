package net.serlith.purpur.tasks.stats;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.data.DataStorage;
import net.serlith.purpur.listeners.ServerListener;
import net.serlith.purpur.tasks.AbstractPerformanceTask;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@NullMarked
public class TpsBarTask extends AbstractPerformanceTask {

    private static @Nullable TpsBarTask INSTANCE;
    public static TpsBarTask getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("TpsBar has not yet been initialized");
        }
        return INSTANCE;
    }

    @Getter
    private double tps = 20.0;
    @Getter
    private double mspt = 0.0;
    private int tick = 0;

    @Getter
    private double tpsMin = 20.0;
    @Getter
    private double tpsMax = 20.0;
    @Getter
    private double tps50Percentile = 20.0;
    @Getter
    private double tps95Percentile = 20.0;

    @Getter
    private double msptMin = 0.0;
    @Getter
    private double msptMax = 0.0;
    @Getter
    private double mspt50Percentile = 0.0;
    @Getter
    private double mspt95Percentile = 0.0;

    public TpsBarTask(PurpurBars plugin) {
        super(plugin);
        INSTANCE = this;
    }

    @Override
    public BossBar createBossBar() {
        return BossBar.bossBar(Component.empty(), 0F, getInstance().getBossBarColor(0), RootConfig.getInstance().format.tpsBar.progressOverlay);
    }

    @Override
    public void updateBossBar(BossBar bossBar, Player player) {
        int ping = player.getPing();
        bossBar.progress(this.getPercent(this.tps, this.mspt, ping));
        bossBar.color(this.getBossBarColor(ping));
        bossBar.name(MiniMessage.miniMessage().deserialize(RootConfig.getInstance().format.tpsBar.title,
                Placeholder.component("tps", this.getTpsColor(this.tps)),
                Placeholder.component("mspt", this.getMsptColor(this.mspt)),
                Placeholder.component("ping", this.getPingColor(ping))
                ,
                Placeholder.component("tps-min", this.getTpsColor(this.tpsMin)),
                Placeholder.component("tps-max", this.getTpsColor(this.tpsMax)),
                Placeholder.component("tps-50ile", this.getTpsColor(this.tps50Percentile)),
                Placeholder.component("tps-95ile", this.getTpsColor(this.tps95Percentile)),

                Placeholder.component("mspt-min", this.getMsptColor(this.msptMin)),
                Placeholder.component("mspt-max", this.getMsptColor(this.msptMax)),
                Placeholder.component("mspt-50ile", this.getMsptColor(this.mspt50Percentile)),
                Placeholder.component("mspt-95ile", this.getMsptColor(this.mspt95Percentile))
        ));
    }

    @Override
    public Type getType() {
        return Type.TPS_BAR;
    }

    @Override
    public void run() {
        if (++this.tick % RootConfig.getInstance().format.tpsBar.updateInterval != 0) return;

        this.tps = Math.clamp(ServerListener.TPS_AVERAGE.getAverage(), 0.0, 20.0);
        this.mspt = Math.max(0.0, ServerListener.MSPT_AVERAGE.getAverage());

        this.tpsMin = Math.clamp(ServerListener.TPS_AVERAGE.getMin(), 0.0, 20.0);
        this.tpsMax = Math.clamp(ServerListener.TPS_AVERAGE.getMax(), 0.0, 20.0);
        this.tps50Percentile = Math.clamp(ServerListener.TPS_AVERAGE.getPercentile(0.5), 0.0, 20.0);
        this.tps95Percentile = Math.clamp(ServerListener.TPS_AVERAGE.getPercentile(0.95), 0.0, 20.0);

        this.msptMin = Math.max(ServerListener.MSPT_AVERAGE.getMin(), 0.0);
        this.msptMax = Math.max(ServerListener.MSPT_AVERAGE.getMax(), 0.0);
        this.mspt50Percentile = Math.max(ServerListener.MSPT_AVERAGE.getPercentile(0.5), 0.0);
        this.mspt95Percentile = Math.max(ServerListener.MSPT_AVERAGE.getPercentile(0.95), 0.0);

        super.run();
    }

    @Override
    public void dumpAllPlayerUUIDs() {
        DataStorage.getInstance().tpsBar = this.getAllPlayerUUIDs();
    }

    @Override
    public Set<UUID> loadAllPlayerUUIDs() {
        return DataStorage.getInstance().tpsBar;
    }

    private BossBar.Color getBossBarColor(int ping) {
        BossBar.Color color;
        if (this.isGood(ping)) {
            color = RootConfig.getInstance().format.tpsBar.progressColor.good;
        } else if (this.isMedium(ping)) {
            color = RootConfig.getInstance().format.tpsBar.progressColor.medium;
        } else {
            color = RootConfig.getInstance().format.tpsBar.progressColor.low;
        }
        return color;
    }

    @Override
    protected boolean isGood(int ping) {
        return switch (RootConfig.getInstance().format.tpsBar.progressFillMode) {
            case MSPT -> this.mspt < 40;
            case TPS -> this.tps >= 19;
            case PING -> ping < 100;
            default -> false;
        };
    }

    @Override
    protected boolean isMedium(int ping) {
        return switch (RootConfig.getInstance().format.tpsBar.progressFillMode) {
            case MSPT -> this.mspt < 50;
            case TPS -> this.tps >= 15;
            case PING -> ping < 200;
            default -> false;
        };
    }

    private Component getTpsColor(double tps) {
        return MiniMessage.miniMessage().deserialize(this.getTpsHealthColor(RootConfig.getInstance().format.tpsBar.textColor, tps), Placeholder.parsed("text", "%.2f".formatted(tps)));
    }

    private Component getMsptColor(double mspt) {
        return MiniMessage.miniMessage().deserialize(this.getMsptHealthColor(RootConfig.getInstance().format.tpsBar.textColor, mspt), Placeholder.parsed("text", "%.2f".formatted(mspt)));
    }

    private Component getPingColor(int ping) {
        return MiniMessage.miniMessage().deserialize(this.getPingHealthColor(RootConfig.getInstance().format.tpsBar.textColor, ping), Placeholder.parsed("text", "%d".formatted(ping)));
    }

}
