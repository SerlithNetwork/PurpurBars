package net.serlith.purpur.tasks.stats;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.data.DataStorage;
import net.serlith.purpur.tasks.AbstractTask;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class CompassBarTask extends AbstractTask {

    private static CompassBarTask INSTANCE;
    public static CompassBarTask getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("CompassBar has not yet been initialized");
        }
        return INSTANCE;
    }

    @Getter
    private int tick = 0;

    public CompassBarTask(PurpurBars plugin) {
        super(plugin);
        INSTANCE = this;
    }

    @Override
    protected BossBar createBossBar() {
        return BossBar.bossBar(Component.empty(), RootConfig.FORMAT.COMPASS_BAR.PROGRESS_PERCENT, RootConfig.FORMAT.COMPASS_BAR.PROGRESS_COLOR, RootConfig.FORMAT.COMPASS_BAR.PROGRESS_OVERLAY);
    }

    @Override
    protected void updateBossBar(BossBar bossBar, Player player) {
        float yaw = player.getLocation().getYaw();
        int length = RootConfig.FORMAT.COMPASS_BAR.TITLE.length();
        int pos = (int) ((normalize(yaw) * (length / 720F)) + (length / 2F));
        bossBar.name(Component.text(RootConfig.FORMAT.COMPASS_BAR.TITLE.substring(pos - 25, pos + 25)));
    }

    @Override
    public Type getType() {
        return Type.COMPASS_BAR;
    }

    @Override
    public void run() {
        if (++this.tick % RootConfig.FORMAT.COMPASS_BAR.UPDATE_INTERVAL != 0) return;
        super.run();
    }

    @Override
    public void dumpAllPlayerUUIDs() {
        DataStorage.COMPASS_BAR = this.getAllPlayerUUIDs();
    }

    @Override
    public Set<UUID> loadAllPlayerUUIDs() {
        return DataStorage.COMPASS_BAR;
    }

    private float normalize(float yaw) {
        while (yaw < -180.0F) {
            yaw += 360.0F;
        }
        while (yaw > 180.0F) {
            yaw -= 360.0F;
        }
        return yaw;
    }

}
