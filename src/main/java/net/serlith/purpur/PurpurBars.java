package net.serlith.purpur;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.bsommerfeld.jshepherd.core.PersistenceDelegateFactoryRegistry;
import de.bsommerfeld.jshepherd.yaml.YamlPersistenceDelegateFactory;
import io.papermc.paper.ServerBuildInfo;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.serlith.purpur.commands.*;
import net.serlith.purpur.configs.PapiConfig;
import net.serlith.purpur.configs.RegionConfig;
import net.serlith.purpur.configs.WorldConfig;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.data.DataStorage;
import net.serlith.purpur.hooks.PapiHook;
import net.serlith.purpur.listeners.*;
import net.serlith.purpur.schedule.BossBarRunnable;
import net.serlith.purpur.schedule.SystemMonitorRunnable;
import net.serlith.purpur.tasks.region.RegionFollowBarTask;
import net.serlith.purpur.tasks.region.compat.CompatRegionFollowBarTask;
import net.serlith.purpur.tasks.stats.CompassBarTask;
import net.serlith.purpur.tasks.stats.RamBarTask;
import net.serlith.purpur.tasks.stats.TpsBarTask;
import net.serlith.purpur.tasks.world.WorldBarTask;
import net.serlith.purpur.tasks.world.WorldFollowBarTask;
import org.bstats.bukkit.Metrics;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public final class PurpurBars extends JavaPlugin {

    private static ScheduledExecutorService EXECUTOR = null;

    @Getter
    private static PurpurBars instance;
    @Getter
    private static final Component prefix = MiniMessage.miniMessage().deserialize("<gray>[<gradient:#429fff:#d621ff>PurpurBars</gradient>]<gray>");

    @Getter
    private final String namespace = "purpurbars";
    @Getter
    private Path storageFolder;
    @Getter
    private BossBarRunnable barsTask;
    @Getter
    private SystemMonitorRunnable systemMonitorRunnable;

    @Getter
    private boolean supportsPAPI = false;
    @Getter
    private boolean supportsPWT = false;
    @Getter
    private boolean supportsFoliaMetrics = false;


    @Override
    public void onLoad() {
        instance = this;
        this.storageFolder = this.getDataPath().resolve(".storage");
        PersistenceDelegateFactoryRegistry.registerFactory(new YamlPersistenceDelegateFactory());
    }

    @Override
    public void onEnable() {
        RootConfig.initialize(this);
        DataStorage.initialize(this);
        new Metrics(this, 24547);

        new PlayerListener(this);
        new ServerListener(this);

        this.barsTask = new BossBarRunnable(this);
        this.systemMonitorRunnable = new SystemMonitorRunnable();

        if (this.supportsPapiPlaceholders()) {
            PapiConfig.initialize(this);
            new PapiHook(this).register();
            this.getLogger().info("PlaceholderAPI support enabled!");
        }

        String extraFeature = "";
        int threads = 1;
        if (ServerBuildInfo.buildInfo().isBrandCompatible(Key.key("papermc", "folia"))) {
            RegionConfig.initialize(this);
            new PlayerRegionListener(this);
            if (this.supportsFoliaRegionMetrics()) {
                this.barsTask.addTask(new RegionFollowBarTask(this));
            } else {
                this.barsTask.addTask(new CompatRegionFollowBarTask(this));
                threads = 2; // Just to prevent the future joins to delay other tasks
            }
            extraFeature = "+ Folia";
        } else if (this.supportsParallelWorldTicking()) {
            WorldConfig.initialize(this);
            new WorldListener(this);
            this.barsTask.addTask(new WorldFollowBarTask(this));
            Bukkit.getWorlds().forEach(world -> this.barsTask.addWorldTask(world, new WorldBarTask(this, world)));
            extraFeature = "+ PWT";
        }

        EXECUTOR = Executors.newScheduledThreadPool(threads, new ThreadFactoryBuilder()
                .setNameFormat("PurpurBars Worker Thread - %d")
                .setDaemon(false)
                .setPriority(Thread.MIN_PRIORITY)
                .setUncaughtExceptionHandler((t, e) -> this.getSLF4JLogger().error("Uncaught exception in PurpurBars thread", e))
                .build()
        );

        this.barsTask.addTask(new TpsBarTask(this));
        this.barsTask.addTask(new RamBarTask(this));
        this.barsTask.addTask(new CompassBarTask(this));

        this.barsTask.init();
        EXECUTOR.scheduleAtFixedRate(this.barsTask, 0, 50, TimeUnit.MILLISECONDS);
        EXECUTOR.scheduleAtFixedRate(this.systemMonitorRunnable, 0, 1, TimeUnit.SECONDS);

        this.printBanner(extraFeature);
    }

    @Override
    public void onDisable() {
        EXECUTOR.shutdown();
        this.barsTask.stop();
        DataStorage.getInstance().save();
    }


    private void printBanner(String extra) {
        Stream.of(
                "<gradient:#3ba5ff:#9c52ff>    .+--------.+  </gradient>",
                        "<gradient:#2db1ff:#8c60ff>   .'   /    .' | </gradient>",
                        "<gradient:#2eb0ff:#8f5eff>  +--------+' | | </gradient>    <light_purple>▄▖          ▄       </light_purple>",
                        "<gradient:#459cff:#aa47ff>  |   |    |  .'| </gradient>    <light_purple>▙▌▌▌▛▘▛▌▌▌▛▘▙▘▀▌▛▘▛▘</light_purple>",
                        "<gradient:#5c89ff:#ba3aff>  |--------|' | | </gradient>    <light_purple>▌ ▙▌▌ ▙▌▙▌▌ ▙▘█▌▌ ▄▌</light_purple>",
                        "<gradient:#598bff:#c033ff>  |   |    |  .'  </gradient>    <light_purple>      ▌             </light_purple>",
                        "<gradient:#6780ff:#ca2bff>  +--------+'     </gradient>" + (extra.isBlank() ? "" : ("            <gradient:#5c89ff:#ba3aff>" + extra + "</gradient>"))
                )
                .map(MiniMessage.miniMessage()::deserialize)
                .forEach(this.getServer()::sendMessage);
    }

    private boolean supportsPapiPlaceholders() {
        this.supportsPAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        return this.supportsPAPI;
    }

    private boolean supportsParallelWorldTicking() {
        try {
            Server.class.getMethod("isParallelWorldTickingEnabled");
            this.getLogger().info("Parallel World Ticking API found, attempting to hook...");
        } catch (NoSuchMethodException e) {
            return false;
        }

        boolean enabled = Bukkit.getServer().isParallelWorldTickingEnabled();
        if (enabled) {
            this.getLogger().info("Parallel World Ticking support enabled!");
        } else {
            this.getLogger().info("Parallel World Ticking is available but not enabled!");
        }

        try {
            World.class.getMethod("getAverageTickTime");
            this.supportsPWT = true;
        } catch (NoSuchMethodException e) {
            this.getLogger().severe("Your server software does not properly implement the Parallel World Ticking API method: World#getAverageTickTime");
            this.getLogger().severe("Contact the author of: " + Bukkit.getName());
            return false;
        }

        return enabled;
    }

    private boolean supportsFoliaRegionMetrics() {
        try {
            Bukkit.class.getMethod("getRegionTPS", Location.class);
        } catch (NoSuchMethodException e) {
            return false;
        }

        try {
            Bukkit.class.getMethod("getRegionAverageTickTimes", Location.class);
        } catch (NoSuchMethodException ignore) {
            return false;
        }

        this.supportsFoliaMetrics = true;
        return true;
    }

}
