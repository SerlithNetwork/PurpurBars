package net.serlith.purpur.tasks.region.compat;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.concurrent.PurpurBarsThread;
import net.serlith.purpur.configs.RegionConfig;
import net.serlith.purpur.tasks.region.RegionFollowBarTask;
import net.serlith.purpur.util.Tuple3;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public class CompatRegionFollowBarTask extends RegionFollowBarTask {

    public CompatRegionFollowBarTask(PurpurBars plugin) {
        super(plugin);
    }

    @Override
    protected void updateBossBar(BossBar bossBar, Player player) {
        double tps = 20.0;
        double mspt = 0.0;
        int ping = 0;
        if (Thread.currentThread() instanceof PurpurBarsThread) {
            final CompletableFuture<Tuple3<Double, Double, Integer>> future = new CompletableFuture<>();
            player.getScheduler().run(this.plugin, task -> {
                future.complete(new Tuple3<>(
                        Bukkit.getTPS()[0],
                        Bukkit.getAverageTickTime(),
                        player.getPing()
                ));
            }, null);
            final Tuple3<Double, Double, Integer> tuple3 = future.join();
            tps = tuple3.first();
            mspt = tuple3.second();
            ping = tuple3.third();
        }

        bossBar.progress(this.getPercent(tps, mspt, ping));
        bossBar.color(this.getBossBarColor(tps, mspt, ping));
        bossBar.name(MiniMessage.miniMessage().deserialize(RegionConfig.getInstance().format.regionFollowBar.title,
                Placeholder.component("tps", this.getTpsColor(tps)),
                Placeholder.component("mspt", this.getMsptColor(mspt)),
                Placeholder.component("ping", this.getPingColor(ping))
        ));
    }

}
