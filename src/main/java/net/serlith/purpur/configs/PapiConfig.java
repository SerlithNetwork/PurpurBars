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
import net.serlith.purpur.configs.types.PapiBarEntry;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

@NullMarked
@Comment({
        "If you're reading this config, it means you're using PlaceholderAPI in your server",
        "",
        "Configurations marked with \uD83D\uDD25 can be hot-reloaded",
        "Configurations marked with \uD83D\uDD03 require a server-restart",
        "Message configurations only support Adventure's MiniMessage format",
        "Learn more: https://docs.papermc.io/adventure/minimessage/format"
})
@SuppressWarnings({"unused", "FieldMayBeFinal", "FieldCanBeLocal"})
public class PapiConfig extends ConfigurablePojo<PapiConfig> {
    private final transient PurpurBars plugin;
    private PapiConfig(final PurpurBars plugin) {
        this.plugin = plugin;
    }

    private static @Nullable PapiConfig INSTANCE = null;
    public static PapiConfig getInstance() {
        final PapiConfig instance = INSTANCE;
        if (instance == null) {
            throw new IllegalStateException("PlaceholderAPI config has not been initialized");
        }
        return instance;
    }
    public static void runIfInitialized(final Consumer<PapiConfig> consumer) {
        final PapiConfig instance = INSTANCE;
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

        INSTANCE = ConfigurationLoader.from(plugin.getDataPath().resolve("settings-papi.yml"))
                .withComments()
                .load(() -> new PapiConfig(plugin));
        INSTANCE.save();
        INITIALIZED = true;
    }


    @Comment("Configurations for formatting bars")
    @Section("format")
    public Format format = new Format();
    public static class Format {

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
        @Key("papi-bars")
        public List<PapiBarEntry> papiBars = List.of(
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
        @SuppressWarnings({"NotNullFieldNotInitialized"})
        public transient List<String> papiBarsNames;

    }

    @Comment("\uD83D\uDD25 Configurations for message feedback when running a command")
    @Section("messages")
    public Messages messages = new Messages();
    @SuppressWarnings({"NotNullFieldNotInitialized"})
    public static class Messages {

        private String barDoesNotExistString = "<red>This bar does not exist!";
        public transient Component barDoesNotExist;

        @PostInject
        public void convert() {
            this.barDoesNotExist = PurpurBars.getPrefix()
                    .appendSpace()
                    .append(MiniMessage.miniMessage().deserialize(this.barDoesNotExistString));
        }

    }

    @PostInject
    public void convert() {
        this.format.papiBarsNames = this.format.papiBars.stream().map(e -> e.name).toList();

        this.plugin.getBarsTask().removeNotPresentPapiBarTasks(this.format.papiBarsNames);
        this.plugin.getBarsTask().updatePresentPapiBarTasks(this.format.papiBars);
        this.plugin.getBarsTask().addNotPresentPapiBarTasks(this.format.papiBars);

    }

}
