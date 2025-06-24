package net.serlith.purpur.configs;

import net.j4c0b3y.api.config.StaticConfig;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.tasks.BossBarTask;
import net.serlith.purpur.tasks.TpsBarTask;
import org.bukkit.event.EventPriority;

import java.io.File;
import java.util.List;
import java.util.Map;

@StaticConfig.Header({
        "Configurations marked with \uD83D\uDD25 can be hot-reloaded",
        "Configurations marked with \uD83D\uDD03 require a server-restart",
        "Message configurations only support Adventure's MiniMessage format",
        "Learn more: https://docs.advntr.dev/minimessage/format.html"
})
public class RootConfig extends StaticConfig {

    @Ignore
    public static RootConfig INSTANCE;

    public RootConfig(PurpurBars plugin) {
        super(new File(plugin.getDataFolder(), "settings.yml"), plugin.getConfigHandler());
        INSTANCE = this;

        Map.of(
                "", ""
        ).forEach(this::relocate);
    }

    @Comment("❌ Don't touch")
    @SuppressWarnings("unused")
    public static class INFO {

        public static String VERSION = "2.0";

    }

    @Comment("Configurations for formatting bars and commands")
    public static class FORMAT {

        @Comment("TpsBar format configuration")
        public static class TPS_BAR {

            @Comment("\uD83D\uDD25 Title to be shown on the TPS bar")
            public static String TITLE = "<gray>TPS<yellow>:</yellow> <tps> MSPT<yellow>:</yellow> <mspt> Ping<yellow>:</yellow> <ping>ms";

            @Comment("\uD83D\uDD25 Possible overlays: https://jd.advntr.dev/api/4.7.0/net/kyori/adventure/bossbar/BossBar.Overlay.html")
            public static BossBar.Overlay PROGRESS_OVERLAY = BossBar.Overlay.NOTCHED_20;

            @Comment("\uD83D\uDD25 Possible values: TPS, MSPT & PING")
            public static TpsBarTask.ProgressFillMode PROGRESS_FILL_MODE = TpsBarTask.ProgressFillMode.MSPT;

            @Comment("\uD83D\uDD25 Delay (in ticks) between bar update")
            public static int TICK_INTERVAL = 20;

            @Comment("\uD83D\uDD25 Possible colors: https://jd.advntr.dev/api/4.7.0/net/kyori/adventure/bossbar/BossBar.Color.html")
            public static class PROGRESS_COLOR {
                public static BossBar.Color GOOD = BossBar.Color.GREEN;
                public static BossBar.Color MEDIUM = BossBar.Color.YELLOW;
                public static BossBar.Color LOW = BossBar.Color.RED;
            }

            @Comment("\uD83D\uDD25 Color format for texts, the placeholder <text> represents the content")
            public static class TEXT_COLOR {
                public static String GOOD = "<gradient:#55ff55:#00aa00><text></gradient>";
                public static String MEDIUM = "<gradient:#ffff55:#ffaa00><text></gradient>";
                public static String LOW = "<gradient:#ff5555:#aa0000><text></gradient>";
            }

        }

        @Comment("RamBar format configuration")
        public static class RAM_BAR {

            @Comment("\uD83D\uDD25 Title to be shown on the RAM bar")
            public static String TITLE = "<gray>Ram<yellow>:</yellow> <used>/<xmx> (<percent>)";

            @Comment("\uD83D\uDD25 Possible overlays: https://jd.advntr.dev/api/4.7.0/net/kyori/adventure/bossbar/BossBar.Overlay.html")
            public static BossBar.Overlay PROGRESS_OVERLAY = BossBar.Overlay.NOTCHED_20;

            @Comment("\uD83D\uDD25 Delay (in ticks) between bar update")
            public static int TICK_INTERVAL = 20;

            @Comment("\uD83D\uDD25 Possible colors: https://jd.advntr.dev/api/4.7.0/net/kyori/adventure/bossbar/BossBar.Color.html")
            public static class PROGRESS_COLOR {
                public static BossBar.Color GOOD = BossBar.Color.GREEN;
                public static BossBar.Color MEDIUM = BossBar.Color.YELLOW;
                public static BossBar.Color LOW = BossBar.Color.RED;
            }

            @Comment("\uD83D\uDD25 Color format for texts, the placeholder <text> represents the content")
            public static class TEXT_COLOR {
                public static String GOOD = "<gradient:#55ff55:#00aa00><text></gradient>";
                public static String MEDIUM = "<gradient:#ffff55:#ffaa00><text></gradient>";
                public static String LOW = "<gradient:#ff5555:#aa0000><text></gradient>";
            }

        }

        @Comment("Ram command format configuration")
        public static class RAM {

            @Comment({
                    "\uD83D\uDD25 If you like the original Purpur output, use this single following line:",
                    "- \"<green>Ram Usage: <used>/<xmx> (<percent>)\""
            })
            public static List<String> OUTPUT = List.of(
                    "",
                    "<color:#eb6ae8>Ram Usage",
                    "",
                    "<bar> <green><used>/<xmx> (<percent>)",
                    ""
            );

            @Comment("Configurations for output usage bar")
            public static class USAGE_BAR {

                @Comment("\uD83D\uDD25 If your custom characters are too wide, you can reduce this")
                public static int LENGTH = 80;

                @Comment("\uD83D\uDD25 Characters used to format the bar")
                public static class CHARS {
                    public static String BAR = "|";
                    public static String START = "┨";
                    public static String END = "┠";
                }

                @Comment("\uD83D\uDD25 Hex colors used in the progressbar")
                public static class COLOR {

                    public static String USED = "#f099ee";
                    @Ignore
                    public static TextColor _USED = TextColor.fromHexString("#f099ee");


                    public static String UNUSED = "#ababab";
                    @Ignore
                    public static TextColor _UNUSED = TextColor.fromHexString("#ababab");


                    public static String BORDER = "#4a4a4a";
                    @Ignore
                    public static TextColor _BORDER = TextColor.fromHexString("#4a4a4a");

                }

            }

        }

    }

    @Comment("Configurations to apply bars when PlayerJoinEvent is called")
    public static class JOIN_EVENT {

        @Comment({
                "\uD83D\uDD03 Priority on which the bars will re-apply after joining the server again",
                "This feature can help prevent these bars from interfering with other plugin's bossbars"
        })
        public static EventPriority PRIORITY = EventPriority.MONITOR;

        @Comment({
                "\uD83D\uDD25 If empty, bars will apply in any order",
                "Possible values: TPS_BAR, RAM_BAR",
                "Example: [TPS_BAR, RAM_BAR] will place the TPS bar above the RAM bar"
        })
        public static List<BossBarTask.Type> ORDER = List.of();

        @Comment({
                "\uD83D\uDD25 Enables saving tpsbar/rambar across restarts",
                "Due to some limitations, it can cause a minor performance impact if too many players have access to these bars"
        })
        public static boolean SAVE_ACROSS_RESTARTS = false;

    }

    @Comment("\uD83D\uDD25 Configurations for message feedback when running a command")
    public static class MESSAGES {

        public static String NO_PERMISSION = "<red>You have no permission to run this command";
        @Ignore
        public static Component _NO_PERMISSION = Component.empty();


        public static String NOT_FOUND = "<red>Command not found";
        @Ignore
        public static Component _NOT_FOUND = Component.empty();


        public static String NOT_PLAYER = "<red>This command can only be used by a player";
        @Ignore
        public static Component _NOT_PLAYER = Component.empty();


        public static String FAILED_RELOAD = "<red>Failed to load configuration!";
        @Ignore
        public static Component _FAILED_RELOAD = Component.empty();


        public static String SUCCESSFUL_RELOAD = "<green>Configuration reloaded!";
        @Ignore
        public static Component _SUCCESSFUL_RELOAD = Component.empty();

    }

    @Override
    public void load() {
        super.load();

        // Load components
        FORMAT.RAM.USAGE_BAR.COLOR._USED = TextColor.fromHexString(FORMAT.RAM.USAGE_BAR.COLOR.USED);
        FORMAT.RAM.USAGE_BAR.COLOR._UNUSED = TextColor.fromHexString(FORMAT.RAM.USAGE_BAR.COLOR.UNUSED);
        FORMAT.RAM.USAGE_BAR.COLOR._BORDER = TextColor.fromHexString(FORMAT.RAM.USAGE_BAR.COLOR.BORDER);

        MESSAGES._NO_PERMISSION = MiniMessage.miniMessage().deserialize(MESSAGES.NO_PERMISSION);
        MESSAGES._NOT_FOUND = MiniMessage.miniMessage().deserialize(MESSAGES.NOT_FOUND);
        MESSAGES._NOT_PLAYER = MiniMessage.miniMessage().deserialize(MESSAGES.NOT_PLAYER);
        MESSAGES._FAILED_RELOAD = MiniMessage.miniMessage().deserialize(MESSAGES.FAILED_RELOAD);
        MESSAGES._SUCCESSFUL_RELOAD = MiniMessage.miniMessage().deserialize(MESSAGES.SUCCESSFUL_RELOAD);

    }

}
