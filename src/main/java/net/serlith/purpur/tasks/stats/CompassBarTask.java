package net.serlith.purpur.tasks.stats;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.data.DataStorage;
import net.serlith.purpur.tasks.AbstractTask;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@NullMarked
public class CompassBarTask extends AbstractTask {

    private static @Nullable CompassBarTask INSTANCE;
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
        return BossBar.bossBar(Component.empty(),
                RootConfig.getInstance().format.compassBar.progressPercent,
                RootConfig.getInstance().format.compassBar.progressColor,
                RootConfig.getInstance().format.compassBar.progressOverlay
        );
    }

    @Override
    protected void updateBossBar(BossBar bossBar, Player player) {
        final String title = RootConfig.getInstance().format.compassBar.title;
        final float yaw = player.getLocation().getYaw();
        final int length = title.length();
        final int pos = (int) ((normalize(yaw) * (length / 720F)) + (length / 2F));
        bossBar.name(Component.text(title.substring(pos - 25, pos + 25)));
    }

    @Override
    public Type getType() {
        return Type.COMPASS_BAR;
    }

    @Override
    public void run() {
        if (++this.tick % RootConfig.getInstance().format.compassBar.updateInterval != 0) return;
        super.run();
    }

    @Override
    public void dumpAllPlayerUUIDs() {
        DataStorage.getInstance().compassBar = this.getAllPlayerUUIDs();
    }

    @Override
    public Set<UUID> loadAllPlayerUUIDs() {
        return DataStorage.getInstance().compassBar;
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
