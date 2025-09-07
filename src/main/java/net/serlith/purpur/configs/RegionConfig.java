package net.serlith.purpur.configs;

import net.j4c0b3y.api.config.StaticConfig;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.tasks.region.RegionBarTask;
import net.serlith.purpur.tasks.region.RegionFollowBarTask;

import java.io.File;

@StaticConfig.Header({
        "If you're reading this config, it means you're running Folia",
        "",
        "Configurations marked with \uD83D\uDD25 can be hot-reloaded",
        "Configurations marked with \uD83D\uDD03 require a server-restart",
        "Message configurations only support Adventure's MiniMessage format",
        "Learn more: https://docs.advntr.dev/minimessage/format.html"
})
public class RegionConfig extends StaticConfig {

    @Ignore
    public static RegionConfig INSTANCE;

    @Ignore
    private final PurpurBars plugin;

    public RegionConfig(PurpurBars plugin) {
        super(new File(plugin.getDataFolder(), "settings-region.yml"), plugin.getConfigHandler());
        INSTANCE = this;
        this.plugin = plugin;
    }

    @Priority(1)
    public static class FORMAT {

        @Comment({
                "RegionBar format configuration",
                "RegionBar is equivalent to TpsBar but applies to the current region that owns the player",
                "TpsBar is still usable but will reflect the main thread, which is no longer relevant in Folia"
        })
        public static class REGION_BAR {

            @Comment("\uD83D\uDD25 Title to be shown on the TPS bar")
            public static String TITLE = "<gray>MSPT<yellow>:</yellow> <mspt> Player<yellow>:</yellow> [<player>] Ping<yellow>:</yellow> <ping>ms";

            @Comment("\uD83D\uDD25 Possible overlays: https://jd.advntr.dev/api/4.7.0/net/kyori/adventure/bossbar/BossBar.Overlay.html")
            public static BossBar.Overlay PROGRESS_OVERLAY = BossBar.Overlay.NOTCHED_20;

            @Comment("\uD83D\uDD25 Possible values: TPS, MSPT & PING")
            public static RegionBarTask.ProgressFillMode PROGRESS_FILL_MODE = RegionBarTask.ProgressFillMode.MSPT;

            @Comment("\uD83D\uDD25 Delay (in ticks) between bar updates on the player screen")
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

        public static class REGION_FOLLOW_BAR {

            @Comment("\uD83D\uDD25 Title to be shown on the TPS bar")
            public static String TITLE = "<gray>TPS<yellow>:</yellow> <tps> MSPT<yellow>:</yellow> <mspt> Ping<yellow>:</yellow> <ping>ms";

            @Comment("\uD83D\uDD25 Possible overlays: https://jd.advntr.dev/api/4.7.0/net/kyori/adventure/bossbar/BossBar.Overlay.html")
            public static BossBar.Overlay PROGRESS_OVERLAY = BossBar.Overlay.NOTCHED_20;

            @Comment("\uD83D\uDD25 Possible values: TPS, MSPT & PING")
            public static RegionFollowBarTask.ProgressFillMode PROGRESS_FILL_MODE = RegionFollowBarTask.ProgressFillMode.MSPT;

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

        public static String PLAYER_DOES_NOT_EXIST = "<red>This player is not online!";
        @Ignore
        public static Component _PLAYER_DOES_NOT_EXIST = Component.empty();

    }

    @Override
    public void load() {
        if (this.plugin.getGetRegionAverageTickTimes() == null && !new File(this.plugin.getDataFolder(), "settings-region.yml").exists()) {
            this.plugin.getLogger().warning("");
            this.plugin.getLogger().warning(" You have loaded PurpurBars in a Folia server that doesn't provide a MSPT API");
            this.plugin.getLogger().warning(" Placeholders for region MSPT will not be available in this version");
            this.plugin.getLogger().warning(" You won't see this warning again!");
            this.plugin.getLogger().warning("");
        }
        super.load();

        MESSAGES._PLAYER_DOES_NOT_EXIST = MiniMessage.miniMessage().deserialize(MESSAGES.PLAYER_DOES_NOT_EXIST);

    }

}
