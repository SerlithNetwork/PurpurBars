package net.serlith.purpur.tasks.region;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RegionConfig;
import net.serlith.purpur.data.DataStorage;
import net.serlith.purpur.tasks.AbstractTask;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.UUID;

public class RegionFollowBarTask extends AbstractTask {

    private static RegionFollowBarTask INSTANCE;
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
        return BossBar.bossBar(Component.empty(), 0F, getInstance().getBossBarColor(20.0, 0.0, 0), RegionConfig.FORMAT.REGION_FOLLOW_BAR.PROGRESS_OVERLAY);
    }

    @Override
    protected void updateBossBar(BossBar bossBar, Player player) {
        double tps = 0.0, mspt = 0.0;
        try {
            tps = ((double[]) this.plugin.getGetRegionTPS().invoke(null, player.getLocation()))[0];
            mspt = this.plugin.getGetRegionAverageTickTimes() == null ? 0.0 : ((double[]) this.plugin.getGetRegionAverageTickTimes().invoke(null, player.getLocation()))[0];
        } catch (IllegalAccessException | InvocationTargetException ignore) {}

        int ping = player.getPing();
        bossBar.progress(this.getPercent(tps, mspt, ping));
        bossBar.color(this.getBossBarColor(tps, mspt, ping));
        bossBar.name(MiniMessage.miniMessage().deserialize(RegionConfig.FORMAT.REGION_FOLLOW_BAR.TITLE,
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
        if (++this.tick % RegionConfig.FORMAT.REGION_FOLLOW_BAR.UPDATE_INTERVAL != 0) return;
        super.run();
    }

    @Override
    public Set<UUID> loadAllPlayerUUIDs() {
        return DataStorage.REGION_FOLLOW_BAR;
    }

    @Override
    public void dumpAllPlayerUUIDs() {
        DataStorage.REGION_FOLLOW_BAR = this.getAllPlayerUUIDs();
    }

    private float getPercent(double tps, double mspt, int ping) {
        return switch (RegionConfig.FORMAT.REGION_FOLLOW_BAR.PROGRESS_FILL_MODE) {
            case MSPT -> Math.max(Math.min(((float) mspt) / 50F, 1F), 0F);
            case TPS -> Math.max(Math.min(((float) tps) / 20F, 1F), 0F);
            case PING -> Math.max(Math.min(((float) ping) / 200F, 1F), 0F);
        };
    }

    private BossBar.Color getBossBarColor(double tps, double mspt, int ping) {
        BossBar.Color color;
        if (this.isGood(tps, mspt, ping)) {
            color = RegionConfig.FORMAT.REGION_FOLLOW_BAR.PROGRESS_COLOR.GOOD;
        } else if (this.isMedium(tps, mspt, ping)) {
            color = RegionConfig.FORMAT.REGION_FOLLOW_BAR.PROGRESS_COLOR.MEDIUM;
        } else {
            color = RegionConfig.FORMAT.REGION_FOLLOW_BAR.PROGRESS_COLOR.LOW;
        }
        return color;
    }

    private boolean isGood(double tps, double mspt, int ping) {
        return switch (RegionConfig.FORMAT.REGION_FOLLOW_BAR.PROGRESS_FILL_MODE) {
            case MSPT -> mspt < 40;
            case TPS -> tps >= 19;
            case PING -> ping < 100;
        };
    }

    private boolean isMedium(double tps, double mspt, int ping) {
        return switch (RegionConfig.FORMAT.REGION_FOLLOW_BAR.PROGRESS_FILL_MODE) {
            case MSPT -> mspt < 50;
            case TPS -> tps >= 15;
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
            colored = RegionConfig.FORMAT.REGION_FOLLOW_BAR.TEXT_COLOR.GOOD;
        } else if (tps >= 15) {
            colored = RegionConfig.FORMAT.REGION_FOLLOW_BAR.TEXT_COLOR.MEDIUM;
        } else {
            colored = RegionConfig.FORMAT.REGION_FOLLOW_BAR.TEXT_COLOR.LOW;
        }
        return colored;
    }

    private String getMsptHealthColor(double mspt) {
        String colored;
        if (mspt < 40) {
            colored = RegionConfig.FORMAT.REGION_FOLLOW_BAR.TEXT_COLOR.GOOD;
        } else if (mspt < 50) {
            colored = RegionConfig.FORMAT.REGION_FOLLOW_BAR.TEXT_COLOR.MEDIUM;
        } else {
            colored = RegionConfig.FORMAT.REGION_FOLLOW_BAR.TEXT_COLOR.LOW;
        }
        return colored;
    }

    private String getPingHealthColor(double ping) {
        String colored;
        if (ping < 100) {
            colored = RegionConfig.FORMAT.REGION_FOLLOW_BAR.TEXT_COLOR.GOOD;
        } else if (ping < 200) {
            colored = RegionConfig.FORMAT.REGION_FOLLOW_BAR.TEXT_COLOR.MEDIUM;
        } else {
            colored = RegionConfig.FORMAT.REGION_FOLLOW_BAR.TEXT_COLOR.LOW;
        }
        return colored;
    }

    public enum ProgressFillMode { TPS, MSPT, PING }

}
