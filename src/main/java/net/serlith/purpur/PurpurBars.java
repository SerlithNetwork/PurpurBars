package net.serlith.purpur;

import lombok.Getter;
import net.j4c0b3y.api.config.ConfigHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.serlith.purpur.commands.*;
import net.serlith.purpur.configs.RegionConfig;
import net.serlith.purpur.configs.WorldConfig;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.configs.providers.WorldBarEntryProvider;
import net.serlith.purpur.configs.types.WorldBarEntry;
import net.serlith.purpur.data.DataStorage;
import net.serlith.purpur.hooks.PapiHook;
import net.serlith.purpur.listeners.PlayerListener;
import net.serlith.purpur.listeners.PlayerRegionListener;
import net.serlith.purpur.listeners.ServerListener;
import net.serlith.purpur.listeners.WorldListener;
import net.serlith.purpur.schedule.BossBarRunnable;
import net.serlith.purpur.tasks.region.RegionFollowBarTask;
import net.serlith.purpur.tasks.stats.CompassBarTask;
import net.serlith.purpur.tasks.stats.RamBarTask;
import net.serlith.purpur.tasks.stats.TpsBarTask;
import net.serlith.purpur.tasks.world.WorldFollowBarTask;
import org.bstats.bukkit.Metrics;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public final class PurpurBars extends JavaPlugin {

    private static ScheduledExecutorService EXECUTOR = null;

    @Getter
    private final String namespace = "purpurbars";
    @Getter
    private ConfigHandler configHandler;
    @Getter
    private Component prefix;
    @Getter
    private File storageFolder;
    @Getter
    private BossBarRunnable barsTask;

    @Getter
    private Method getWorldAverageTickTime;
    @Getter
    private Method getRegionTPS;
    @Getter
    private Method getRegionAverageTickTimes;


    @Override
    public void onLoad() {
        this.configHandler = new ConfigHandler();
        this.prefix = MiniMessage.miniMessage().deserialize("<gray>[<gradient:#429fff:#d621ff>PurpurBars</gradient>]<gray> ");
        this.storageFolder = new File(getDataFolder(), "storage");

        this.configHandler.bind(WorldBarEntry.class, new WorldBarEntryProvider());
    }

    @Override
    public void onEnable() {
        new RootConfig(this).load();
        new DataStorage(this).load();
        new Metrics(this, 24547);

        new MainCommand(this);
        new TpsBarCommand(this);
        new RamBarCommand(this);
        new CompassCommand(this);
        new RamCommand(this);
        new PlayerListener(this);
        new ServerListener(this);

        EXECUTOR = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("PurpurBars Worker Thread");
            thread.setDaemon(false);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.setUncaughtExceptionHandler((t, e) -> {
                this.getLogger().severe("Uncaught exception in PurpurBars thread");
                Arrays.stream(e.getStackTrace()).map(Object::toString).forEach(this.getLogger()::severe);
            });
            return thread;
        });
        this.barsTask = new BossBarRunnable();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PapiHook(this).register();
        }

        String extraFeature = "";
        if (this.supportsFoliaRegions()) {
            new RegionConfig(this).load();
            new PlayerRegionListener(this);
            new RegionCommand(this);
            this.barsTask.addTask(new RegionFollowBarTask(this));
            extraFeature = "+ Folia";
        } else if (this.supportsParallelWorldTicking()) {
            new WorldConfig(this).load();
            new WorldListener(this);
            new WorldBarCommand(this);
            this.barsTask.addTask(new WorldFollowBarTask(this));
            extraFeature = "+ PWT";
        }

        this.barsTask.addTask(new TpsBarTask(this));
        this.barsTask.addTask(new RamBarTask(this));
        this.barsTask.addTask(new CompassBarTask(this));

        this.barsTask.init();
        EXECUTOR.scheduleAtFixedRate(this.barsTask, 0, 50, TimeUnit.MILLISECONDS);

        this.printBanner(extraFeature);
    }

    @Override
    public void onDisable() {
        EXECUTOR.shutdown();
        this.barsTask.stop();
        DataStorage.INSTANCE.save();
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

    @SuppressWarnings("all")
    private boolean supportsParallelWorldTicking() {
        Method isParallelWorldTickingEnabled = null;
        try {
            isParallelWorldTickingEnabled = Server.class.getMethod("isParallelWorldTickingEnabled");
            this.getLogger().info("Parallel World Ticking API found, attempting to hook...");
        } catch (NoSuchMethodException e) {
            return false;
        }

        boolean enabled;
        try {
            enabled = (boolean) isParallelWorldTickingEnabled.invoke(Bukkit.getServer());
        } catch (InvocationTargetException | IllegalAccessException exception) {
            this.getLogger().severe("Failed to hook Parallel World Ticking API");
            return false;
        }

        if (enabled){
            this.getLogger().info("Parallel World Ticking support enabled!");
        } else {
            this.getLogger().info("Parallel World Ticking is available but not enabled!");
        }

        try {
            this.getWorldAverageTickTime = World.class.getMethod("getAverageTickTime");
        } catch (NoSuchMethodException e) {
            this.getLogger().severe("Your server software does not properly implement the Parallel World Ticking API method: World#getAverageTickTime");
            this.getLogger().severe("Contact the author of: " + Bukkit.getName());
            return false;
        }

        return enabled;
    }

    @SuppressWarnings("all")
    private boolean supportsFoliaRegions() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            this.getLogger().info("Folia API found, attempting to hook...");
        } catch (ClassNotFoundException e) {
            return false;
        }
        try {
            this.getRegionTPS = Bukkit.class.getMethod("getRegionTPS", Location.class);
        } catch (NoSuchMethodException e) {
            this.getLogger().severe("Failed to hook Folia TPS API, you might be running an old unsupported version");
            return false;
        }

        try {
            this.getRegionAverageTickTimes = Bukkit.class.getMethod("getRegionAverageTickTimes", Location.class);
        } catch (NoSuchMethodException ignore) {
            this.getLogger().info("Folia MSPT API not found, region MSPT placeholders will not be available");
        }

        this.getLogger().info("Folia support enabled!");

        return true;
    }

}
