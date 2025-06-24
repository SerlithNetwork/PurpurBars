package net.serlith.purpur.tasks;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.data.DataStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class TpsBarTask extends BossBarTask {

    private static TpsBarTask INSTANCE;
    public static TpsBarTask getInstance(PurpurBars plugin) {
        if (INSTANCE == null) {
            INSTANCE = new TpsBarTask(plugin);
        }
        return INSTANCE;
    }

    private final PurpurBars plugin;

    @Getter
    private double tps = 20.0;
    @Getter
    private double mspt = 0.0;
    @Getter
    private int tick = 0;

    public TpsBarTask(PurpurBars plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public BossBar createBossBar() {
        return BossBar.bossBar(Component.empty(), 0F, getInstance(this.plugin).getBossBarColor(), RootConfig.FORMAT.TPS_BAR.PROGRESS_OVERLAY);
    }

    @Override
    public void updateBossBar(BossBar bossBar, Player player) {
        bossBar.progress(this.getPercent());
        bossBar.color(this.getBossBarColor());
        bossBar.name(MiniMessage.miniMessage().deserialize(RootConfig.FORMAT.TPS_BAR.TITLE,
                Placeholder.component("tps", this.getTpsColor()),
                Placeholder.component("mspt", this.getMsptColor()),
                Placeholder.component("ping", this.getPingColor(player.getPing()))
        ));
    }

    @Override
    public Type getType() {
        return Type.TPS_BAR;
    }

    @Override
    public void run() {
        if (++this.tick % RootConfig.FORMAT.TPS_BAR.TICK_INTERVAL != 0) return;

        this.tps = Math.max(Math.min(Bukkit.getTPS()[0], 20.0), 0.0);
        this.mspt = Bukkit.getAverageTickTime();

        super.run();
    }

    @Override
    public void dumpAndStop() {
        DataStorage.TPS_BAR = this.getAllPlayerUUIDs();
        super.dumpAndStop();
    }

    @Override
    public Set<UUID> loadAllPlayerUUIDs() {
        return DataStorage.TPS_BAR;
    }

    public float getPercent() {
        if (RootConfig.FORMAT.TPS_BAR.PROGRESS_FILL_MODE == ProgressFillMode.MSPT) {
            return Math.max(Math.min(((float) this.mspt) / 50F, 1F), 0F);
        }
        return Math.max(Math.min(((float) this.tps) / 20F, 1F), 0F);
    }

    private BossBar.Color getBossBarColor() {
        BossBar.Color color;
        if (this.isGood(RootConfig.FORMAT.TPS_BAR.PROGRESS_FILL_MODE, 0)) {
            color = RootConfig.FORMAT.TPS_BAR.PROGRESS_COLOR.GOOD;
        } else if (this.isMedium(RootConfig.FORMAT.TPS_BAR.PROGRESS_FILL_MODE, 0)) {
            color = RootConfig.FORMAT.TPS_BAR.PROGRESS_COLOR.MEDIUM;
        } else {
            color = RootConfig.FORMAT.TPS_BAR.PROGRESS_COLOR.LOW;
        }
        return color;
    }

    private boolean isGood(ProgressFillMode mode, int ping) {
        return switch (mode) {
            case MSPT -> this.mspt < 40;
            case TPS -> this.tps >= 19;
            case PING -> ping < 100;
        };
    }

    private boolean isMedium(ProgressFillMode mode, int ping) {
        return switch (mode) {
            case MSPT -> this.mspt < 50;
            case TPS -> this.tps >= 15;
            case PING -> ping < 200;
        };
    }

    private Component getTpsColor() {
        return MiniMessage.miniMessage().deserialize(this.getColor(ProgressFillMode.TPS, 0), Placeholder.parsed("text", "%.2f".formatted(this.tps)));
    }

    private Component getMsptColor() {
        return MiniMessage.miniMessage().deserialize(this.getColor(ProgressFillMode.MSPT, 0), Placeholder.parsed("text", "%.2f".formatted(this.mspt)));
    }

    private Component getPingColor(int ping) {
        return MiniMessage.miniMessage().deserialize(this.getColor(ProgressFillMode.PING, ping), Placeholder.parsed("text", "%d".formatted(ping)));
    }

    private String getColor(ProgressFillMode mode, int ping) {
        String colored;
        if (this.isGood(mode, ping)) {
            colored = RootConfig.FORMAT.TPS_BAR.TEXT_COLOR.GOOD;
        } else if (this.isMedium(mode, ping)) {
            colored = RootConfig.FORMAT.TPS_BAR.TEXT_COLOR.MEDIUM;
        } else {
            colored = RootConfig.FORMAT.TPS_BAR.TEXT_COLOR.LOW;
        }
        return colored;
    }



    public enum ProgressFillMode { TPS, MSPT, PING }

}
