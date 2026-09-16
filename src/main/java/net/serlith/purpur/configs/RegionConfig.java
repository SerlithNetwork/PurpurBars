package net.serlith.purpur.configs;

import de.bsommerfeld.jshepherd.annotation.Comment;
import de.bsommerfeld.jshepherd.annotation.Key;
import de.bsommerfeld.jshepherd.annotation.PostInject;
import de.bsommerfeld.jshepherd.annotation.Section;
import de.bsommerfeld.jshepherd.core.ConfigurablePojo;
import de.bsommerfeld.jshepherd.core.ConfigurationLoader;
import net.kyori.adventure.bossbar.BossBar;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.types.ProgressColorBar;
import net.serlith.purpur.configs.types.ProgressColorText;
import net.serlith.purpur.tasks.AbstractPerformanceTask;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.file.Files;
import java.util.function.Consumer;

@NullMarked
@Comment({
        "If you're reading this config, it means you're running Folia",
        "",
        "==============================================================",
        "                       READ CAREFULLY",
        " Folia per-region TPS and MSPT tracking consume a lot of RAM",
        "  This also applies to TPS or MSPT placeholders from other",
        "  plugins. DO NOT keep multiple region bars active at once",
        "==============================================================",
        "",
        "Configurations marked with \uD83D\uDD25 can be hot-reloaded",
        "Configurations marked with \uD83D\uDD03 require a server-restart",
        "Message configurations only support Adventure's MiniMessage format",
        "Learn more: https://docs.papermc.io/adventure/minimessage/format"
})
@SuppressWarnings({"unused", "FieldMayBeFinal", "FieldCanBeLocal"})
public class RegionConfig extends ConfigurablePojo<RegionConfig> {
    private final transient PurpurBars plugin;
    private RegionConfig(final PurpurBars plugin) {
        this.plugin = plugin;
    }

    private static @Nullable RegionConfig INSTANCE = null;
    public static RegionConfig getInstance() {
        final RegionConfig instance = INSTANCE;
        if (instance == null) {
            throw new IllegalStateException("Region config has not been initialized");
        }
        return instance;
    }
    public static void runIfInitialized(final Consumer<RegionConfig> consumer) {
        final RegionConfig instance = INSTANCE;
        if (instance == null) {
            return;
        }
        consumer.accept(instance);
    }


    private static boolean INITIALIZED = false;
    public static void initialize(final PurpurBars plugin) {
        if (INITIALIZED) {
            return;
        }

        INSTANCE = ConfigurationLoader.from(plugin.getDataPath().resolve("settings-region.yml"))
                .withComments()
                .load(() -> new RegionConfig(plugin));
        INSTANCE.save();
        INITIALIZED = true;
    }

    @Comment("Configurations for formatting bars")
    @Section("format")
    public Format format = new Format();
    public static class Format {

        @Comment({
                "RegionBar format configuration",
                "RegionBar is equivalent to TpsBar but applies to the current region that owns the player",
                "TpsBar is still usable but will reflect the main thread, which is no longer relevant in Folia"
        })
        @Section("region-bar")
        public RegionBar regionBar = new RegionBar();
        public static class RegionBar {

            @Comment("\uD83D\uDD25 Title to be shown on the TPS bar")
            @Key("title")
            public String title = "<gray>MSPT<yellow>:</yellow> <mspt> Player<yellow>:</yellow> [<player>] Ping<yellow>:</yellow> <ping>ms";

            @Comment("\uD83D\uDD25 Possible overlays: https://jd.papermc.io/adventure/5.2.0/net.kyori.adventure.api/net/kyori/adventure/bossbar/BossBar.Overlay.html")
            @Key("progress-overlay")
            public BossBar.Overlay progressOverlay = BossBar.Overlay.NOTCHED_20;

            @Comment("\uD83D\uDD25 Possible values: TPS, MSPT & PING")
            @Key("progress-fill-mode")
            public AbstractPerformanceTask.ProgressFillMode progressFillMode = AbstractPerformanceTask.ProgressFillMode.MSPT;

            @Comment("\uD83D\uDD25 Delay (in ticks) between bar updates on the player screen")
            @Key("update-interval")
            public int updateInterval = 20;

            @Comment("\uD83D\uDD25 Possible colors: https://jd.papermc.io/adventure/5.2.0/net.kyori.adventure.api/net/kyori/adventure/bossbar/BossBar.Color.html")
            @Section("progress-color")
            public ProgressColorBar progressColor = new ProgressColorBar(
                    BossBar.Color.WHITE,
                    BossBar.Color.YELLOW,
                    BossBar.Color.RED
            );

            @Comment("\uD83D\uDD25 Color format for texts, the placeholder <text> represents the content")
            @Section("text-color")
            public ProgressColorText textColor = new ProgressColorText(
                    "<gradient:#aaffff:#77ffff><text></gradient>",
                    "<gradient:#ffff55:#ffaa00><text></gradient>",
                    "<gradient:#ff5555:#aa0000><text></gradient>"
            );

        }

        @Comment({
                "RegionFollowBar format configuration",
                "RegionFollowBars will keep track of performance in the current region you're in",
        })
        @Section("region-follow-bar")
        public RegionFollowBar regionFollowBar = new RegionFollowBar();
        public static class RegionFollowBar {

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

            @Comment("\uD83D\uDD25 Possible colors: https://jd.papermc.io/adventure/5.2.0/net.kyori.adventure.api/net/kyori/adventure/bossbar/BossBar.Color.html")
            @Section("progress-color")
            public ProgressColorBar progressColor = new ProgressColorBar(
                    BossBar.Color.BLUE,
                    BossBar.Color.YELLOW,
                    BossBar.Color.RED
            );

            @Comment("\uD83D\uDD25 Color format for texts, the placeholder <text> represents the content")
            @Section("text-color")
            public ProgressColorText textColor = new ProgressColorText(
                    "<gradient:#55ffff:#00aaaa><text></gradient>",
                    "<gradient:#ffff55:#ffaa00><text></gradient>",
                    "<gradient:#ff5555:#aa0000><text></gradient>"
            );

        }

    }

    @PostInject
    public void validate() {
        if (!this.plugin.isSupportsFoliaMSPT() && !Files.exists(this.plugin.getDataPath().resolve("settings-region.yml"))) {
            this.plugin.getLogger().warning("");
            this.plugin.getLogger().warning(" You have loaded PurpurBars in a Folia server that doesn't provide a MSPT API");
            this.plugin.getLogger().warning(" Placeholders for region MSPT will not be available in this version");
            this.plugin.getLogger().warning(" RegionBar will not display the actual region MSPT");
            this.plugin.getLogger().warning(" You won't see this warning again!");
            this.plugin.getLogger().warning("");
        }
    }

}
