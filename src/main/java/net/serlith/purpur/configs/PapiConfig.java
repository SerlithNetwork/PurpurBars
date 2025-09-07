package net.serlith.purpur.configs;

import net.j4c0b3y.api.config.StaticConfig;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.types.PapiBarEntry;

import java.io.File;
import java.util.List;

@StaticConfig.Header({
        "If you're reading this config, it means you're using PlaceholderAPI in your server",
        "",
        "Configurations marked with \uD83D\uDD25 can be hot-reloaded",
        "Configurations marked with \uD83D\uDD03 require a server-restart",
        "Message configurations only support Adventure's MiniMessage format",
        "Learn more: https://docs.advntr.dev/minimessage/format.html"
})
public class PapiConfig extends StaticConfig {

    @Ignore
    public static PapiConfig INSTANCE;

    @Ignore
    private final PurpurBars plugin;

    public PapiConfig(PurpurBars plugin) {
        super(new File(plugin.getDataFolder(), "settings-papi.yml"), plugin.getConfigHandler());
        this.plugin = plugin;
        INSTANCE = this;
    }

    @Priority(1)
    @Comment({
            "This is a list of custom bars for you to create",
            "Each element follows roughly the same rules as the builtin ones",
            "The provided example consists of a 'name', which must be unique and will be used spawn the bossbar using /papibar <name>",
            "Followed by a title, which supports both Minimessage format and PlaceholderAPI placeholders from any plugin",
            "Min, value and max represent the minimum value, the real-time value and maximum possible value the bar can range, all can be a placeholder or a number",
            "Update interval to configure the refresh rate (in ticks)",
            "Overlay and Colors from MiniMessage enums",
            "And both middle/high bounds that will represent when the bar has to change color"
    })
    public static List<PapiBarEntry> PAPI_BARS = List.of(
            new PapiBarEntry(
                    "mspt",
                    "<gray>Min<yellow>:</yellow> <gradient:#fbbfff:#e88eff>%purpurbars_mspt_min%</gradient> MSPT<yellow>:</yellow> <gradient:#fbbfff:#e88eff>%purpurbars_mspt%</gradient> 95%ile<yellow>:</yellow> <gradient:#fbbfff:#e88eff>%purpurbars_mspt_95ile%</gradient> Max<yellow>:</yellow> <gradient:#fbbfff:#e88eff>%purpurbars_mspt_max%</gradient>",
                    "0",
                    "%purpurbars_mspt%",
                    "50",
                    20,
                    BossBar.Overlay.NOTCHED_20,
                    BossBar.Color.PINK,
                    BossBar.Color.PURPLE,
                    BossBar.Color.RED,
                    0.8,
                    0.9
            )
    );
    @Ignore
    public static List<String> PAPI_BARS_NAMES = List.of("mspt");

    @Priority(2)
    public static class MESSAGES {

        public static String BAR_DOES_NOT_EXIST = "<red>This bar does not exist!";
        @Ignore
        public static Component _BAR_DOES_NOT_EXIST = Component.empty();

    }

    @Override
    public void load() {
        super.load();

        MESSAGES._BAR_DOES_NOT_EXIST = MiniMessage.miniMessage().deserialize(MESSAGES.BAR_DOES_NOT_EXIST);

        PAPI_BARS_NAMES = PAPI_BARS.stream().map(PapiBarEntry::name).toList();
        this.plugin.getBarsTask().removeNotPresentPapiBarTasks(PAPI_BARS_NAMES);
        this.plugin.getBarsTask().updatePresentPapiBarTasks(PAPI_BARS);
        this.plugin.getBarsTask().addNotPresentPapiBarTasks(PAPI_BARS);

    }

}
