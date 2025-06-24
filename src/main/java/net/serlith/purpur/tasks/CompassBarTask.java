package net.serlith.purpur.tasks;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.data.DataStorage;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class CompassBarTask extends BossBarTask {

    private static CompassBarTask INSTANCE;
    public static CompassBarTask getInstance(PurpurBars plugin) {
        if (INSTANCE == null) {
            INSTANCE = new CompassBarTask(plugin);
        }
        return INSTANCE;
    }

    @Getter
    private int tick = 0;

    private CompassBarTask(PurpurBars plugin) {
        super(plugin);
    }

    @Override
    BossBar createBossBar() {
        return BossBar.bossBar(Component.empty(), RootConfig.FORMAT.COMPASS_BAR.PROGRESS_PERCENT, RootConfig.FORMAT.COMPASS_BAR.PROGRESS_COLOR, RootConfig.FORMAT.COMPASS_BAR.PROGRESS_OVERLAY);
    }

    @Override
    void updateBossBar(BossBar bossBar, Player player) {
        float yaw = player.getLocation().getYaw();
        int length = RootConfig.FORMAT.COMPASS_BAR.TITLE.length();
        int pos = (int) ((normalize(yaw) * (length / 720F)) + (length / 2F));
        bossBar.name(Component.text(RootConfig.FORMAT.COMPASS_BAR.TITLE.substring(pos - 25, pos + 25)));
    }

    @Override
    Type getType() {
        return Type.COMPASS_BAR;
    }

    @Override
    public void run() {
        if (++this.tick % RootConfig.FORMAT.COMPASS_BAR.TICK_INTERVAL != 0) return;
        super.run();
    }

    @Override
    public void dumpAndStop() {
        DataStorage.COMPASS_BAR = this.getAllPlayerUUIDs();
        super.dumpAndStop();
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
