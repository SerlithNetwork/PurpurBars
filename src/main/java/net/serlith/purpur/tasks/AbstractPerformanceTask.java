package net.serlith.purpur.tasks;

import net.kyori.adventure.bossbar.BossBar;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.types.ProgressColorBar;
import net.serlith.purpur.configs.types.ProgressColorText;

public abstract class AbstractPerformanceTask extends AbstractTask {

    public AbstractPerformanceTask(PurpurBars plugin) {
        super(plugin);
    }

    protected ProgressFillMode getProgressFillMode() {
        return ProgressFillMode.NONE;
    }

    protected float getPercent(final double tps, final double mspt, final int ping) {
        return switch (this.getProgressFillMode()) {
            case TPS -> Math.clamp(((float) tps) / 20F, 0F, 1F);
            case MSPT -> Math.clamp(((float) mspt) / 50F, 0F, 1F);
            case PING -> Math.clamp(((float) ping) / 200F, 0F, 1F);
            default -> 1.0F;
        };
    }

    protected BossBar.Color getBossBarColor(final ProgressColorBar progress, final int ping) {
        BossBar.Color color;
        if (this.isGood(ping)) {
            color = progress.good;
        } else if (this.isMedium(ping)) {
            color = progress.medium;
        } else {
            color = progress.low;
        }
        return color;
    }

    protected boolean isGood(final int ping) {
        return false;
    }

    protected boolean isMedium(final int ping) {
        return false;
    }

    protected String getTpsHealthColor(final ProgressColorText color, final double tps) {
        String colored;
        if (tps >= 19) {
            colored = color.good;
        } else if (tps >= 15) {
            colored = color.medium;
        } else {
            colored = color.low;
        }
        return colored;
    }

    protected String getMsptHealthColor(final ProgressColorText color, final double mspt) {
        String colored;
        if (mspt < 40) {
            colored = color.good;
        } else if (mspt < 50) {
            colored = color.medium;
        } else {
            colored = color.low;
        }
        return colored;
    }

    protected String getPingHealthColor(final ProgressColorText color, final double ping) {
        String colored;
        if (ping < 100) {
            colored = color.good;
        } else if (ping < 200) {
            colored = color.medium;
        } else {
            colored = color.low;
        }
        return colored;
    }

    public enum ProgressFillMode { TPS, MSPT, PING, NONE }

}
