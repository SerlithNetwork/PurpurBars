package net.serlith.purpur.configs;

import net.j4c0b3y.api.config.StaticConfig;
import net.j4c0b3y.api.config.platform.adventure.types.PrefixedComponent;
import net.kyori.adventure.bossbar.BossBar;
import net.serlith.purpur.PurpurBars;

import java.io.File;

@StaticConfig.Header({
        "If you're reading this config, it means your server software supports Parallel World Ticking",
        "",
        "Configurations marked with \uD83D\uDD25 can be hot-reloaded",
        "Configurations marked with \uD83D\uDD03 require a server-restart",
        "Message configurations only support Adventure's MiniMessage format",
        "Learn more: https://docs.papermc.io/adventure/minimessage/format"
})
public class WorldConfig extends StaticConfig {

    @Ignore
    public static WorldConfig INSTANCE;

    public WorldConfig(PurpurBars plugin) {
        super(new File(plugin.getDataFolder(), "settings-world.yml"), plugin.getConfigHandler());
        INSTANCE = this;
    }

    @Priority(1)
    public static class FORMAT {

        @Comment({
                "WorldBar format configuration",
                "WorldBars can be created to keep track of performance in other worlds",
        })
        public static class WORLD_BAR {

            @Comment("\uD83D\uDD25 Title to be shown on the World MSPT bar")
            public static String TITLE = "<gray>MSPT<yellow>:</yellow> <mspt> World<yellow>:</yellow> [<world>]";

            @Comment("\uD83D\uDD25 Possible overlays: https://jd.advntr.dev/api/4.7.0/net/kyori/adventure/bossbar/BossBar.Overlay.html")
            public static BossBar.Overlay PROGRESS_OVERLAY = BossBar.Overlay.NOTCHED_20;

            @Comment("\uD83D\uDD25 Delay (in ticks) between bar updates")
            public static int UPDATE_INTERVAL = 20;

            @Comment("\uD83D\uDD25 Possible colors: https://jd.advntr.dev/api/4.7.0/net/kyori/adventure/bossbar/BossBar.Color.html")
            public static class PROGRESS_COLOR {
                public static BossBar.Color GOOD = BossBar.Color.WHITE;
                public static BossBar.Color MEDIUM = BossBar.Color.YELLOW;
                public static BossBar.Color LOW = BossBar.Color.RED;
            }

            @Comment("\uD83D\uDD25 Color format for texts, the placeholder <text> represents the content")
            public static class TEXT_COLOR {
                public static String GOOD = "<gradient:#aaffff:#77ffff><text></gradient>";
                public static String MEDIUM = "<gradient:#ffff55:#ffaa00><text></gradient>";
                public static String LOW = "<gradient:#ff5555:#aa0000><text></gradient>";
            }

        }

        @Comment({
                "WorldFollowBar format configuration",
                "WorldFollowBars will keep track of performance in the current world you're in",
        })
        public static class WORLD_FOLLOW_BAR {

            @Comment("\uD83D\uDD25 Title to be shown on the World MSPT bar")
            public static String TITLE = "<gray>MSPT<yellow>:</yellow> <mspt> World<yellow>:</yellow> [<world>]";

            @Comment("\uD83D\uDD25 Possible overlays: https://jd.advntr.dev/api/4.7.0/net/kyori/adventure/bossbar/BossBar.Overlay.html")
            public static BossBar.Overlay PROGRESS_OVERLAY = BossBar.Overlay.NOTCHED_20;

            @Comment("\uD83D\uDD25 Delay (in ticks) between bar updates on the player screen")
            public static int UPDATE_INTERVAL = 20;

            @Comment("\uD83D\uDD25 Possible colors: https://jd.advntr.dev/api/4.7.0/net/kyori/adventure/bossbar/BossBar.Color.html")
            public static class PROGRESS_COLOR {
                public static BossBar.Color GOOD = BossBar.Color.BLUE;
                public static BossBar.Color MEDIUM = BossBar.Color.YELLOW;
                public static BossBar.Color LOW = BossBar.Color.RED;
            }

            @Comment("\uD83D\uDD25 Color format for texts, the placeholder <text> represents the content")
            public static class TEXT_COLOR {
                public static String GOOD = "<gradient:#55ffff:#00aaaa><text></gradient>";
                public static String MEDIUM = "<gradient:#ffff55:#ffaa00><text></gradient>";
                public static String LOW = "<gradient:#ff5555:#aa0000><text></gradient>";
            }

        }

    }

    @Priority(2)
    public static class MESSAGES {

        public static PrefixedComponent WORLD_DOES_NOT_EXIST = new PrefixedComponent("<red>This world does not exist! Was it unloaded?");

    }

}
