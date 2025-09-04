package net.serlith.purpur.listeners;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.util.RollingAverage;
import net.serlith.purpur.util.TpsRollingAverage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

// Referenced from both Minecraft internals and Spark
public class ServerListener implements Listener {

    private static final long SEC_IN_NANO = TimeUnit.SECONDS.toNanos(1);
    private static final int TPS_SAMPLE_INTERVAL = 20;
    private static final BigDecimal TPS_BASE = new BigDecimal(SEC_IN_NANO).multiply(new BigDecimal(TPS_SAMPLE_INTERVAL));

    public static final TpsRollingAverage TPS_AVERAGE = new TpsRollingAverage(RootConfig.FORMAT.TPS_BAR.TPS_SAMPLING_INTERVAL);
    public static final RollingAverage MSPT_AVERAGE = new RollingAverage(20 * RootConfig.FORMAT.TPS_BAR.MSPT_SAMPLING_INTERVAL);

    private int tick = 0;
    private long last = 0;

    public ServerListener(PurpurBars plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTickStart(ServerTickStartEvent event) {
        if (this.tick++ % TPS_SAMPLE_INTERVAL != 0) return;

        long now = System.nanoTime();
        if (this.last == 0) {
            this.last = now;
            return;
        }

        long diff = now - this.last;
        if (diff <= 0) return;

        BigDecimal currentTps = TPS_BASE.divide(new BigDecimal(diff), 30, RoundingMode.HALF_UP);
        BigDecimal total = currentTps.multiply(new BigDecimal(diff));

        TPS_AVERAGE.add(currentTps, diff, total);

        this.last = now;
    }

    @SuppressWarnings("all")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onTickEnd(ServerTickEndEvent event) {
        MSPT_AVERAGE.add(new BigDecimal(event.getTickDuration()));
    }

}
