package net.serlith.purpur.tasks.region;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RegionConfig;
import net.serlith.purpur.tasks.AbstractTask;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.UUID;

public class RegionBarTask extends AbstractTask {

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
        return BossBar.bossBar(Component.empty(), 0F, this.getBossBarColor(), RegionConfig.FORMAT.REGION_BAR.PROGRESS_OVERLAY);
    }

    @Override
    protected void updateBossBar(BossBar bossBar, Player player) {
        try {
            this.tps = ((double[]) this.plugin.getGetRegionTPS().invoke(null, this.player.getLocation()))[0];
            this.mspt = this.plugin.getGetRegionAverageTickTimes() == null ? 0.0 : ((double[]) this.plugin.getGetRegionAverageTickTimes().invoke(null, this.player.getLocation()))[0];
        } catch (IllegalAccessException | InvocationTargetException ignore) {}

        this.ping = this.player.getPing();
        bossBar.progress(this.getPercent());
        bossBar.color(this.getBossBarColor());
        bossBar.name(MiniMessage.miniMessage().deserialize(RegionConfig.FORMAT.REGION_BAR.TITLE,
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
        if (++this.tick % RegionConfig.FORMAT.REGION_BAR.UPDATE_INTERVAL != 0) return;
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

    private float getPercent() {
        return switch (RegionConfig.FORMAT.REGION_BAR.PROGRESS_FILL_MODE) {
            case MSPT -> Math.max(Math.min(((float) this.mspt) / 50F, 1F), 0F);
            case TPS -> Math.max(Math.min(((float) this.tps) / 20F, 1F), 0F);
            case PING -> Math.max(Math.min(((float) this.ping) / 200F, 1F), 0F);
        };
    }

    private BossBar.Color getBossBarColor() {
        BossBar.Color color;
        if (this.isGood()) {
            color = RegionConfig.FORMAT.REGION_BAR.PROGRESS_COLOR.GOOD;
        } else if (this.isMedium()) {
            color = RegionConfig.FORMAT.REGION_BAR.PROGRESS_COLOR.MEDIUM;
        } else {
            color = RegionConfig.FORMAT.REGION_BAR.PROGRESS_COLOR.LOW;
        }
        return color;
    }

    private boolean isGood() {
        return switch (RegionConfig.FORMAT.REGION_BAR.PROGRESS_FILL_MODE) {
            case MSPT -> mspt < 40;
            case TPS -> tps >= 19;
            case PING -> ping < 100;
        };
    }

    private boolean isMedium() {
        return switch (RegionConfig.FORMAT.REGION_BAR.PROGRESS_FILL_MODE) {
            case MSPT -> mspt < 50;
            case TPS -> tps >= 15;
            case PING -> ping < 200;
        };
    }

    private Component getTpsColor() {
        return MiniMessage.miniMessage().deserialize(this.getTpsHealthColor(), Placeholder.parsed("text", "%.2f".formatted(this.tps)));
    }

    private Component getMsptColor() {
        return MiniMessage.miniMessage().deserialize(this.getMsptHealthColor(), Placeholder.parsed("text", "%.2f".formatted(this.mspt)));
    }

    private Component getPlayerColor() {
        return MiniMessage.miniMessage().deserialize(this.getMsptHealthColor(), Placeholder.parsed("text", this.player.getName()));
    }

    private Component getPingColor() {
        return MiniMessage.miniMessage().deserialize(this.getPingHealthColor(), Placeholder.parsed("text", "%d".formatted(this.ping)));
    }

    private String getTpsHealthColor() {
        String colored;
        if (this.tps >= 19) {
            colored = RegionConfig.FORMAT.REGION_BAR.TEXT_COLOR.GOOD;
        } else if (this.tps >= 15) {
            colored = RegionConfig.FORMAT.REGION_BAR.TEXT_COLOR.MEDIUM;
        } else {
            colored = RegionConfig.FORMAT.REGION_BAR.TEXT_COLOR.LOW;
        }
        return colored;
    }

    private String getMsptHealthColor() {
        String colored;
        if (this.mspt < 40) {
            colored = RegionConfig.FORMAT.REGION_BAR.TEXT_COLOR.GOOD;
        } else if (this.mspt < 50) {
            colored = RegionConfig.FORMAT.REGION_BAR.TEXT_COLOR.MEDIUM;
        } else {
            colored = RegionConfig.FORMAT.REGION_BAR.TEXT_COLOR.LOW;
        }
        return colored;
    }

    private String getPingHealthColor() {
        String colored;
        if (this.ping < 100) {
            colored = RegionConfig.FORMAT.REGION_BAR.TEXT_COLOR.GOOD;
        } else if (this.ping < 200) {
            colored = RegionConfig.FORMAT.REGION_BAR.TEXT_COLOR.MEDIUM;
        } else {
            colored = RegionConfig.FORMAT.REGION_BAR.TEXT_COLOR.LOW;
        }
        return colored;
    }

    public enum ProgressFillMode { TPS, MSPT, PING }

}
