package net.serlith.purpur.tasks.stats;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.data.DataStorage;
import net.serlith.purpur.tasks.AbstractTask;
import net.serlith.purpur.util.Utils;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.management.ManagementFactory;
import java.util.Set;
import java.util.UUID;

@NullMarked
public class RamBarTask extends AbstractTask {

    private static @Nullable RamBarTask INSTANCE;
    public static RamBarTask getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("RamBar has not yet been initialized");
        }
        return INSTANCE;
    }

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
    private int tick = 0;

    public RamBarTask(PurpurBars plugin) {
        super(plugin);
        INSTANCE = this;
    }

    @Override
    public BossBar createBossBar() {
        return BossBar.bossBar(Component.empty(), 0F, getInstance().getBossBarColor(), RootConfig.getInstance().format.ramBar.progressOverlay);
    }

    @Override
    public void updateBossBar(BossBar bossBar, Player player) {
        bossBar.progress(this.getPercent());
        bossBar.color(this.getBossBarColor());
        bossBar.name(MiniMessage.miniMessage().deserialize(RootConfig.getInstance().format.ramBar.title,
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
        if (++this.tick % RootConfig.getInstance().format.ramBar.updateInterval != 0) return;

        var heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        this.allocated = heap.getCommitted();
        this.used = heap.getUsed();
        this.xmx = heap.getMax();
        this.xms = heap.getInit();
        this.percent = Math.clamp((float) this.used / this.xmx, 0F, 1F);

        super.run();
    }

    @Override
    public void dumpAllPlayerUUIDs() {
        DataStorage.getInstance().ramBar = this.getAllPlayerUUIDs();
    }

    @Override
    public Set<UUID> loadAllPlayerUUIDs() {
        return DataStorage.getInstance().ramBar;
    }

    private BossBar.Color getBossBarColor() {
        BossBar.Color color;
        if (this.percent < 0.5F) {
            color = RootConfig.getInstance().format.ramBar.progressColor.good;
        } else if (this.percent < 0.75F) {
            color = RootConfig.getInstance().format.ramBar.progressColor.medium;
        } else {
            color = RootConfig.getInstance().format.ramBar.progressColor.low;
        }
        return color;
    }

    public Component format(long v) {
        String colored;
        if (this.percent < 0.6F) {
            colored = RootConfig.getInstance().format.ramBar.textColor.good;
        } else if (this.percent < 0.85F) {
            colored = RootConfig.getInstance().format.ramBar.textColor.medium;
        } else {
            colored = RootConfig.getInstance().format.ramBar.textColor.low;
        }

        return MiniMessage.miniMessage().deserialize(colored,
                Placeholder.unparsed("text", Utils.formatBytes(v))
        );
    }

}
