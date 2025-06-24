package net.serlith.purpur.tasks;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RootConfig;
import org.bukkit.entity.Player;

import java.lang.management.ManagementFactory;

public class RamBarTask extends BossBarTask {

    private static RamBarTask INSTANCE;
    public static RamBarTask getInstance(PurpurBars plugin) {
        if (INSTANCE == null) {
            INSTANCE = new RamBarTask(plugin);
        }
        return INSTANCE;
    }

    private final PurpurBars plugin;

    @Getter
    private long allocated = 0L;
    @Getter
    private long used = 0L;
    @Getter
    private long xmx = 0L;
    @Getter
    private long xms = 0L;
    @Getter
    private float percent = 0F;
    @Getter
    private int tick = 0;

    public RamBarTask(PurpurBars plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public BossBar createBossBar() {
        return BossBar.bossBar(Component.empty(), 0F, getInstance(this.plugin).getBossBarColor(), RootConfig.FORMAT.RAM_BAR.PROGRESS_OVERLAY);
    }

    @Override
    public void updateBossBar(BossBar bossBar, Player player) {
        bossBar.progress(this.getPercent());
        bossBar.color(this.getBossBarColor());
        bossBar.name(MiniMessage.miniMessage().deserialize(RootConfig.FORMAT.RAM_BAR.TITLE,
                Placeholder.component("allocated", this.format(this.allocated)),
                Placeholder.component("used", this.format(this.used)),
                Placeholder.component("xmx", this.format(this.xmx)),
                Placeholder.component("xms", this.format(this.xms)),
                Placeholder.parsed("percent", "%d%%".formatted((int) (this.percent * 100)))
        ));
    }

    @Override
    public Type getType() {
        return Type.RAM_BAR;
    }

    @Override
    public void run() {
        if (++this.tick % RootConfig.FORMAT.RAM_BAR.TICK_INTERVAL != 0) return;

        var heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        this.allocated = heap.getCommitted();
        this.used = heap.getUsed();
        this.xmx = heap.getMax();
        this.xms = heap.getInit();
        this.percent = Math.max(Math.min((float) this.used / this.xmx, 1F), 0F);

        super.run();
    }

    private BossBar.Color getBossBarColor() {
        BossBar.Color color;
        if (this.percent < 0.5F) {
            color = RootConfig.FORMAT.RAM_BAR.PROGRESS_COLOR.GOOD;
        } else if (this.percent < 0.75F) {
            color = RootConfig.FORMAT.RAM_BAR.PROGRESS_COLOR.MEDIUM;
        } else {
            color = RootConfig.FORMAT.RAM_BAR.PROGRESS_COLOR.LOW;
        }
        return color;
    }

    private final char[] sizes = { 'B', 'K', 'M', 'G', 'T', 'P', 'E' };
    public Component format(long v) {
        String colored;
        if (this.percent < 0.6F) {
            colored = RootConfig.FORMAT.RAM_BAR.TEXT_COLOR.GOOD;
        } else if (this.percent < 0.85F) {
            colored = RootConfig.FORMAT.RAM_BAR.TEXT_COLOR.MEDIUM;
        } else {
            colored = RootConfig.FORMAT.RAM_BAR.TEXT_COLOR.LOW;
        }

        String value;
        if (v < 1024) {
            value = "%dB".formatted(v);
        } else {
            var z = (63 - Long.numberOfLeadingZeros(v)) / 10;
            if (z > 2) {
                value = "%.1f%c".formatted(((float) v) / (1L << (z * 10)), sizes[z]);
            } else {
                value = "%d%c".formatted(v / (1L << (z * 10)), sizes[z]);
            }
        }

        return MiniMessage.miniMessage().deserialize(colored,
                Placeholder.unparsed("text", value)
        );
    }

}
