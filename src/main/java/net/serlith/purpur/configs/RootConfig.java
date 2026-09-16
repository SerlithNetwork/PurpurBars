package net.serlith.purpur.configs;

import de.bsommerfeld.jshepherd.annotation.Comment;
import de.bsommerfeld.jshepherd.annotation.Key;
import de.bsommerfeld.jshepherd.annotation.PostInject;
import de.bsommerfeld.jshepherd.annotation.Section;
import de.bsommerfeld.jshepherd.core.ConfigurablePojo;
import de.bsommerfeld.jshepherd.core.ConfigurationLoader;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.types.ProgressColorBar;
import net.serlith.purpur.configs.types.ProgressColorText;
import net.serlith.purpur.configs.types.UsageCharBar;
import net.serlith.purpur.configs.types.UsageColorBar;
import net.serlith.purpur.tasks.AbstractPerformanceTask;
import net.serlith.purpur.tasks.AbstractTask;
import org.bukkit.event.EventPriority;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
@Comment({
        "Configurations marked with \uD83D\uDD25 can be hot-reloaded",
        "Configurations marked with \uD83D\uDD03 require a server-restart",
        "Message configurations only support Adventure's MiniMessage format",
        "Learn more: https://docs.papermc.io/adventure/minimessage/format"
})
@SuppressWarnings({"unused", "FieldMayBeFinal", "FieldCanBeLocal"})
public class RootConfig extends ConfigurablePojo<RootConfig> {
    private RootConfig() {
    }

    @SuppressWarnings("NullAway.Init")
    private static RootConfig INSTANCE;
    public static RootConfig getInstance() {
        return INSTANCE;
    }

    private static boolean INITIALIZED = false;
    public static void initialize(final PurpurBars plugin) {
        if (INITIALIZED) {
            return;
        }

        INSTANCE = ConfigurationLoader.from(plugin.getDataPath().resolve("settings.yml"))
                .withComments()
                .load(RootConfig::new);
        INSTANCE.save();
        INITIALIZED = true;
    }

    @Comment("Configurations for formatting bars and commands")
    @Section("format")
    public Format format = new Format();
    public static class Format {

        @Comment("TpsBar format configuration")
        @Section("tps-bar")
        public TpsBar tpsBar = new TpsBar();
        public static class TpsBar {

            @Comment("\uD83D\uDD25 Title to be shown on the TPS bar")
            @Key("title")
            public String title = "<gray>TPS<yellow>:</yellow> <tps> MSPT<yellow>:</yellow> <mspt> Ping<yellow>:</yellow> <ping>ms";

            @Comment("\uD83D\uDD25 Possible overlays: https://jd.papermc.io/adventure/5.2.0/net.kyori.adventure.api/net/kyori/adventure/bossbar/BossBar.Overlay.html")
            @Key("progress-overlay")
            public BossBar.Overlay progressOverlay = BossBar.Overlay.NOTCHED_20;

            @Comment("\uD83D\uDD25 Possible values: TPS, MSPT & PING")
            @Key("progress-fill-mode")
            public AbstractPerformanceTask.ProgressFillMode progressFillMode = AbstractPerformanceTask.ProgressFillMode.MSPT;

            @Comment("\uD83D\uDD25 Delay (in ticks) between bar updates on the player screen")
            @Key("update-interval")
            public int updateInterval = 20;

            @Comment("\uD83D\uDD03 Sampling time interval (in seconds) for TPS. If 5, this means 5 second average, max, 95%ile...")
            @Key("tps-sampling-interval")
            public int tpsSamplingInterval = 5;

            @Comment("\uD83D\uDD03 Sampling time interval (in seconds) for MSPT. If 5, this means 5 second average, max, 95%ile...")
            @Key("mspt-sampling-interval")
            public int msptSamplingInterval = 5;

            @Comment("\uD83D\uDD25 Possible colors: https://jd.papermc.io/adventure/5.2.0/net.kyori.adventure.api/net/kyori/adventure/bossbar/BossBar.Color.html")
            @Section("progress-color")
            public ProgressColorBar progressColor = new ProgressColorBar(
                    BossBar.Color.GREEN,
                    BossBar.Color.YELLOW,
                    BossBar.Color.RED
            );

            @Comment("\uD83D\uDD25 Color format for texts, the placeholder <text> represents the content")
            @Section("text-color")
            public ProgressColorText textColor = new ProgressColorText(
                    "<gradient:#55ff55:#00aa00><text></gradient>",
                    "<gradient:#ffff55:#ffaa00><text></gradient>",
                    "<gradient:#ff5555:#aa0000><text></gradient>"
            );

        }

        @Comment("RamBar format configuration")
        @Section("ram-bar")
        public RamBar ramBar = new RamBar();
        public static class RamBar {

            @Comment("\uD83D\uDD25 Title to be shown on the RAM bar")
            @Key("title")
            public String title = "<gray>Ram<yellow>:</yellow> <used>/<xmx> (<percent>)";

            @Comment("\uD83D\uDD25 Possible overlays: https://jd.papermc.io/adventure/5.2.0/net.kyori.adventure.api/net/kyori/adventure/bossbar/BossBar.Overlay.html")
            @Key("progress-overlay")
            public BossBar.Overlay progressOverlay = BossBar.Overlay.NOTCHED_20;

            @Comment("\uD83D\uDD25 Delay (in ticks) between bar updates on the player screen")
            @Key("update-interval")
            public int updateInterval = 20;

            @Comment("\uD83D\uDD25 Possible colors: https://jd.papermc.io/adventure/5.2.0/net.kyori.adventure.api/net/kyori/adventure/bossbar/BossBar.Color.html")
            @Section("progress-color")
            public ProgressColorBar progressColor = new ProgressColorBar(
                    BossBar.Color.GREEN,
                    BossBar.Color.YELLOW,
                    BossBar.Color.RED
            );

            @Comment("\uD83D\uDD25 Color format for texts, the placeholder <text> represents the content")
            @Section("text-color")
            public ProgressColorText textColor = new ProgressColorText(
                    "<gradient:#55ff55:#00aa00><text></gradient>",
                    "<gradient:#ffff55:#ffaa00><text></gradient>",
                    "<gradient:#ff5555:#aa0000><text></gradient>"
            );

        }

        @Comment("CompassBar format configuration")
        @Section("compass-bar")
        public CompassBar compassBar = new CompassBar();
        public static class CompassBar {

            @Comment("\uD83D\uDD25 Title to be shown on the COMPASS bar")
            @Key("title")
            public String title = "S  ·  ◈  ·  ◈  ·  ◈  ·  SW  ·  ◈  ·  ◈  ·  ◈  ·  W  ·  ◈  ·  ◈  ·  ◈  ·  NW  ·  ◈  ·  ◈  ·  ◈  ·  N  ·  ◈  ·  ◈  ·  ◈  ·  NE  ·  ◈  ·  ◈  ·  ◈  ·  E  ·  ◈  ·  ◈  ·  ◈  ·  SE  ·  ◈  ·  ◈  ·  ◈  ·  S  ·  ◈  ·  ◈  ·  ◈  ·  SW  ·  ◈  ·  ◈  ·  ◈  ·  W  ·  ◈  ·  ◈  ·  ◈  ·  NW  ·  ◈  ·  ◈  ·  ◈  ·  N  ·  ◈  ·  ◈  ·  ◈  ·  NE  ·  ◈  ·  ◈  ·  ◈  ·  E  ·  ◈  ·  ◈  ·  ◈  ·  SE  ·  ◈  ·  ◈  ·  ◈  ·  ";

            @Comment("\uD83D\uDD25 Possible overlays: https://jd.papermc.io/adventure/5.2.0/net.kyori.adventure.api/net/kyori/adventure/bossbar/BossBar.Overlay.html")
            @Key("progress-overlay")
            public BossBar.Overlay progressOverlay = BossBar.Overlay.PROGRESS;

            @Comment("\uD83D\uDD25 Delay (in ticks) between bar updates on the player screen")
            @Key("update-interval")
            public int updateInterval = 5;

            @Comment("\uD83D\uDD25 Possible colors: https://jd.papermc.io/adventure/5.2.0/net.kyori.adventure.api/net/kyori/adventure/bossbar/BossBar.Color.html")
            @Key("progress-color")
            public BossBar.Color progressColor = BossBar.Color.BLUE;

            @Comment("\uD83D\uDD25 How full the bar should be, can take any number from 0.0 to 1.0")
            @Key("progress-percent")
            public float progressPercent = 1.0f;

        }

        @Comment("Ram command format configuration")
        @Section("ram")
        public Ram ram = new Ram();
        public static class Ram {

            @Comment({
                    "\uD83D\uDD25 If you like the original Purpur output, use this single following line:",
                    "- \"<green>Ram Usage: <used>/<xmx> (<percent>)\""
            })
            @Key("output")
            public List<String> output = List.of(
                    "",
                    "<color:#eb6ae8>Ram Usage",
                    "",
                    "<bar> <green><used>/<xmx> (<percent>)",
                    ""
            );

            @Comment("Configurations for output usage bar")
            @Section("usage-bar")
            public UsageBar usageBar = new UsageBar();
            public static class UsageBar {

                @Comment("\uD83D\uDD25 If your custom characters are too wide, you can reduce this")
                @Key("length")
                public int length = 80;

                @Comment("\uD83D\uDD25 Characters used to format the bar")
                @Key("chars")
                public UsageCharBar chars = new UsageCharBar(
                        "|",
                        "┨",
                        "┠"
                );

                @Comment("\uD83D\uDD25 Hex colors used in the progressbar")
                @Section("color")
                public UsageColorBar color = new UsageColorBar(
                        "#f099ee",
                        "#ababab",
                        "#4a4a4a"
                );

            }

        }

    }

    @Comment("Configurations to apply bars when PlayerJoinEvent is called")
    @Section("join-event")
    public JoinEvent joinEvent = new JoinEvent();
    public static class JoinEvent {

        @Comment({
                "\uD83D\uDD03 Priority on which the bars will re-apply after joining the server again",
                "This feature can help prevent these bars from interfering with other plugin's bossbars",
                "Possible values: LOWEST, LOW, NORMAL, HIGH, HIGHEST, MONITOR",
                "Source: https://jd.papermc.io/paper/26.2/org/bukkit/event/EventPriority.html"
        })
        public EventPriority priority = EventPriority.MONITOR;

        @Comment({
                "\uD83D\uDD25 If empty, bars will apply in any order when re-joining",
                "Possible values: TPS_BAR, RAM_BAR, COMPASS_BAR, WORLD_FOLLOW_BAR, REGION_BAR",
                "Example: [TPS_BAR, RAM_BAR] will place the TPS bar above the RAM bar"
        })
        public List<AbstractTask.Type> order = List.of();

    }

    @Comment("\uD83D\uDD25 Configurations for message feedback when running a command")
    @Section("messages")
    public Messages messages = new Messages();
    @SuppressWarnings({"NotNullFieldNotInitialized"})
    public static class Messages {

        private String failedReloadString = "<red>Failed to load configuration!";
        public transient Component failedReload;

        private String successfulReloadString = "<green>Configuration reloaded!";
        public transient Component successfulReload;

        @PostInject
        public void convert() {
            this.failedReload = PurpurBars.getPrefix()
                    .appendSpace()
                    .append(MiniMessage.miniMessage().deserialize(this.failedReloadString));
            this.successfulReload = PurpurBars.getPrefix()
                    .appendSpace()
                    .append(MiniMessage.miniMessage().deserialize(this.successfulReloadString));
        }

    }

}
