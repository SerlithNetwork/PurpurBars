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
import net.serlith.purpur.tasks.AbstractTask;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class TpsBarTask extends AbstractTask {

    private static TpsBarTask INSTANCE;
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
        return BossBar.bossBar(Component.empty(), 0F, getInstance().getBossBarColor(0), RootConfig.FORMAT.TPS_BAR.PROGRESS_OVERLAY);
    }

    @Override
    public void updateBossBar(BossBar bossBar, Player player) {
        int ping = player.getPing();
        bossBar.progress(this.getPercent(ping));
        bossBar.color(this.getBossBarColor(ping));
        bossBar.name(MiniMessage.miniMessage().deserialize(RootConfig.FORMAT.TPS_BAR.TITLE,
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
        if (++this.tick % RootConfig.FORMAT.TPS_BAR.UPDATE_INTERVAL != 0) return;

        this.tps = Math.max(Math.min(ServerListener.TPS_AVERAGE.getAverage(), 20.0), 0.0);
        this.mspt = Math.max(0.0, ServerListener.MSPT_AVERAGE.getAverage());

        this.tpsMin = Math.max(Math.min(ServerListener.TPS_AVERAGE.getMin(), 20.0), 0.0);
        this.tpsMax = Math.max(Math.min(ServerListener.TPS_AVERAGE.getMax(), 20.0), 0.0);
        this.tps50Percentile = Math.max(Math.min(ServerListener.TPS_AVERAGE.getPercentile(0.5), 20.0), 0.0);
        this.tps95Percentile = Math.max(Math.min(ServerListener.TPS_AVERAGE.getPercentile(0.95), 20.0), 0.0);

        this.msptMin = Math.max(ServerListener.MSPT_AVERAGE.getMin(), 0.0);
        this.msptMax = Math.max(ServerListener.MSPT_AVERAGE.getMax(), 0.0);
        this.mspt50Percentile = Math.max(ServerListener.MSPT_AVERAGE.getPercentile(0.5), 0.0);
        this.mspt95Percentile = Math.max(ServerListener.MSPT_AVERAGE.getPercentile(0.95), 0.0);

        super.run();
    }

    @Override
    public void dumpAllPlayerUUIDs() {
        DataStorage.TPS_BAR = this.getAllPlayerUUIDs();
    }

    @Override
    public Set<UUID> loadAllPlayerUUIDs() {
        return DataStorage.TPS_BAR;
    }

    private float getPercent(int ping) {
        return switch (RootConfig.FORMAT.TPS_BAR.PROGRESS_FILL_MODE) {
            case MSPT -> Math.max(Math.min(((float) this.mspt) / 50F, 1F), 0F);
            case TPS -> Math.max(Math.min(((float) this.tps) / 20F, 1F), 0F);
            case PING -> Math.max(Math.min(((float) ping) / 200F, 1F), 0F);
        };
    }

    private BossBar.Color getBossBarColor(int ping) {
        BossBar.Color color;
        if (this.isGood(ping)) {
            color = RootConfig.FORMAT.TPS_BAR.PROGRESS_COLOR.GOOD;
        } else if (this.isMedium(ping)) {
            color = RootConfig.FORMAT.TPS_BAR.PROGRESS_COLOR.MEDIUM;
        } else {
            color = RootConfig.FORMAT.TPS_BAR.PROGRESS_COLOR.LOW;
        }
        return color;
    }

    private boolean isGood(int ping) {
        return switch (RootConfig.FORMAT.TPS_BAR.PROGRESS_FILL_MODE) {
            case MSPT -> this.mspt < 40;
            case TPS -> this.tps >= 19;
            case PING -> ping < 100;
        };
    }

    private boolean isMedium(int ping) {
        return switch (RootConfig.FORMAT.TPS_BAR.PROGRESS_FILL_MODE) {
            case MSPT -> this.mspt < 50;
            case TPS -> this.tps >= 15;
            case PING -> ping < 200;
        };
    }

    private Component getTpsColor(double tps) {
        return MiniMessage.miniMessage().deserialize(this.getTpsHealthColor(tps), Placeholder.parsed("text", "%.2f".formatted(tps)));
    }

    private Component getMsptColor(double mspt) {
        return MiniMessage.miniMessage().deserialize(this.getMsptHealthColor(mspt), Placeholder.parsed("text", "%.2f".formatted(mspt)));
    }

    private Component getPingColor(int ping) {
        return MiniMessage.miniMessage().deserialize(this.getPingHealthColor(ping), Placeholder.parsed("text", "%d".formatted(ping)));
    }

    private String getTpsHealthColor(double tps) {
        String colored;
        if (tps >= 19) {
            colored = RootConfig.FORMAT.TPS_BAR.TEXT_COLOR.GOOD;
        } else if (tps >= 15) {
            colored = RootConfig.FORMAT.TPS_BAR.TEXT_COLOR.MEDIUM;
        } else {
            colored = RootConfig.FORMAT.TPS_BAR.TEXT_COLOR.LOW;
        }
        return colored;
    }

    private String getMsptHealthColor(double mspt) {
        String colored;
        if (mspt < 40) {
            colored = RootConfig.FORMAT.TPS_BAR.TEXT_COLOR.GOOD;
        } else if (mspt < 50) {
            colored = RootConfig.FORMAT.TPS_BAR.TEXT_COLOR.MEDIUM;
        } else {
            colored = RootConfig.FORMAT.TPS_BAR.TEXT_COLOR.LOW;
        }
        return colored;
    }

    private String getPingHealthColor(double ping) {
        String colored;
        if (ping < 100) {
            colored = RootConfig.FORMAT.TPS_BAR.TEXT_COLOR.GOOD;
        } else if (ping < 200) {
            colored = RootConfig.FORMAT.TPS_BAR.TEXT_COLOR.MEDIUM;
        } else {
            colored = RootConfig.FORMAT.TPS_BAR.TEXT_COLOR.LOW;
        }
        return colored;
    }



    public enum ProgressFillMode { TPS, MSPT, PING }

}
