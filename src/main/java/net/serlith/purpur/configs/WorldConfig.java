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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

@NullMarked
@Comment({
        "If you're reading this config, it means your server software supports Parallel World Ticking",
        "",
        "Configurations marked with \uD83D\uDD25 can be hot-reloaded",
        "Configurations marked with \uD83D\uDD03 require a server-restart",
        "Message configurations only support Adventure's MiniMessage format",
        "Learn more: https://docs.papermc.io/adventure/minimessage/format"
})
@SuppressWarnings({"unused", "FieldMayBeFinal", "FieldCanBeLocal"})
public class WorldConfig extends ConfigurablePojo<WorldConfig> {
    private WorldConfig() {
    }

    private static @Nullable WorldConfig INSTANCE = null;
    public static WorldConfig getInstance() {
        final WorldConfig instance = INSTANCE;
        if (instance == null) {
            throw new IllegalStateException("World config has not been initialized");
        }
        return instance;
    }
    public static void runIfInitialized(final Consumer<WorldConfig> consumer) {
        final WorldConfig instance = INSTANCE;
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

        INSTANCE = ConfigurationLoader.from(plugin.getDataPath().resolve("settings-world.yml"))
                .withComments()
                .load(WorldConfig::new);
        INSTANCE.save();
        INITIALIZED = true;
    }

    @Comment("Configurations for formatting bars")
    @Section("format")
    public Format format = new Format();
    public static class Format {

        @Comment({
                "WorldBar format configuration",
                "WorldBars can be created to keep track of performance in other worlds",
        })
        @Section("world-bar")
        public WorldBar worldBar = new WorldBar();
        public static class WorldBar {

            @Comment("\uD83D\uDD25 Title to be shown on the World MSPT bar")
            @Key("title")
            public String title = "<gray>MSPT<yellow>:</yellow> <mspt> World<yellow>:</yellow> [<world>]";

            @Comment("\uD83D\uDD25 Possible overlays: https://jd.papermc.io/adventure/5.2.0/net.kyori.adventure.api/net/kyori/adventure/bossbar/BossBar.Overlay.html")
            @Key("progress-overlay")
            public BossBar.Overlay progressOverlay = BossBar.Overlay.NOTCHED_20;

            @Comment("\uD83D\uDD25 Delay (in ticks) between bar updates")
            @Key("update-interval")
            public int updateInterval = 20;

            @Comment("\uD83D\uDD25 Minecraft world names will display as 'overworld' instead of 'minecraft:overworld'")
            @Key("use-minimal-name")
            public boolean useMinimalName = false;

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
                "WorldFollowBar format configuration",
                "WorldFollowBars will keep track of performance in the current world you're in",
        })
        @Section("world-follow-bar")
        public WorldFollowBar worldFollowBar = new WorldFollowBar();
        public static class WorldFollowBar {

            @Comment("\uD83D\uDD25 Title to be shown on the World MSPT bar")
            @Key("title")
            public String title = "<gray>MSPT<yellow>:</yellow> <mspt> World<yellow>:</yellow> [<world>]";

            @Comment("\uD83D\uDD25 Possible overlays: https://jd.papermc.io/adventure/5.2.0/net.kyori.adventure.api/net/kyori/adventure/bossbar/BossBar.Overlay.html")
            @Key("progress-overlay")
            public BossBar.Overlay progressOverlay = BossBar.Overlay.NOTCHED_20;

            @Comment("\uD83D\uDD25 Delay (in ticks) between bar updates on the player screen")
            @Key("update-interval")
            public int updateInterval = 20;

            @Comment("\uD83D\uDD25 Minecraft world names will display as 'overworld' instead of 'minecraft:overworld'")
            @Key("use-minimal-name")
            public boolean useMinimalName = false;

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

    @Section("messages")
    public Messages messages = new Messages();
    @SuppressWarnings({"NotNullFieldNotInitialized"})
    public static class Messages {

        private String worldDoesNotExistString = "<red>This world does not exist! Was it unloaded?";
        public transient Component worldDoesNotExist;

        @PostInject
        public void convert() {
            this.worldDoesNotExist = PurpurBars.getPrefix()
                    .appendSpace()
                    .append(MiniMessage.miniMessage().deserialize(this.worldDoesNotExistString));
        }

    }

}
