package net.serlith.purpur.tasks.region.compat;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.concurrent.PurpurBarsThread;
import net.serlith.purpur.configs.RegionConfig;
import net.serlith.purpur.tasks.region.RegionBarTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public class CompatRegionBarTask extends RegionBarTask {

    public CompatRegionBarTask(PurpurBars plugin, Player player) {
        super(plugin, player);
    }

    @Override
    protected void updateBossBar(BossBar bossBar, Player player) {
        if (Thread.currentThread() instanceof PurpurBarsThread) {
            final CompletableFuture<Boolean> future = new CompletableFuture<>();
            this.player.getScheduler().run(this.plugin, task -> {
                this.tps = Bukkit.getTPS()[0];
                this.mspt = Bukkit.getAverageTickTime();
                this.ping = this.player.getPing();
                future.complete(Boolean.TRUE);
            }, null);
            future.join();
        } else {
            this.tps = 20.0;
            this.mspt = 0.0;
            this.ping = 0;
        }

        bossBar.progress(this.getPercent(this.tps, this.mspt, this.ping));
        bossBar.color(this.getBossBarColor());
        bossBar.name(MiniMessage.miniMessage().deserialize(RegionConfig.getInstance().format.regionBar.title,
                Placeholder.component("tps", this.getTpsColor()),
                Placeholder.component("mspt", this.getMsptColor()),
                Placeholder.component("player", this.getPlayerColor()),
                Placeholder.component("ping", this.getPingColor())
        ));
    }

}
