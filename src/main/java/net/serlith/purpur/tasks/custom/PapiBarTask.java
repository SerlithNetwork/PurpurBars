package net.serlith.purpur.tasks.custom;

import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.types.PapiBarEntry;
import net.serlith.purpur.tasks.AbstractTask;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class PapiBarTask extends AbstractTask {

    @Setter
    private PapiBarEntry entry;
    private int tick = 0;

    public PapiBarTask(PurpurBars plugin, PapiBarEntry entry) {
        super(plugin);
        this.entry = entry;
    }

    @Override
    protected BossBar createBossBar() {
        return BossBar.bossBar(Component.empty(), 0F, this.entry.colorLow(), this.entry.overlay());
    }

    @Override
    protected void updateBossBar(BossBar bossBar, Player player) {
        float percent = this.getPercent(player);
        bossBar.progress(percent);
        bossBar.color(this.getBossBarColor(percent));
        bossBar.name(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, this.entry.title())));
    }

    @Override
    public Type getType() {
        return Type.PAPI_BAR;
    }

    @Override
    public void run() {
        if (++this.tick % this.entry.updateInterval() != 0) return;
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

    private float getPercent(Player player) {
        float min = this.tryParseNumber(player, this.entry.min());
        float value = this.tryParseNumber(player, this.entry.value()) - min;
        float max = this.tryParseNumber(player, this.entry.max()) - min;
        return Math.max(Math.min(value / max, 1F), 0F);
    }

    private float tryParseNumber(Player player, String string) {
        Float min = null;
        try {
            min = Float.parseFloat(string);
        } catch (NumberFormatException ignore) {}
        if (min == null) {
            String parsed = PlaceholderAPI.setPlaceholders(player, string);
            try {
                min = Float.parseFloat(parsed);
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Value '" + string + "' for '" + this.entry.name() + "' is not a number or placeholder that can be parsed into a number");
            }
        }
        return min;
    }

    private BossBar.Color getBossBarColor(float percent) {
        if (percent < this.entry.boundMiddle()) {
            return this.entry.colorLow();
        } else if (percent >= this.entry.boundMiddle() && percent < this.entry.boundHigh()) {
            return this.entry.colorMiddle();
        } else {
            return this.entry.colorHigh();
        }
    }

}
