package net.serlith.purpur;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.serlith.purpur.commands.*;
import org.jspecify.annotations.NullMarked;

@NullMarked
@SuppressWarnings({"UnstableApiUsage", "unused"})
public class PurpurBarsBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {

        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands registrar = event.registrar();

            registrar.register(AdminCommands.buildMainCommand(), "Main PurpurBars administration command");
            registrar.register(CompassCommands.buildCompassCommand(), "Displays a compass using a bossbar");
            registrar.register(RamCommands.buildRamBarCommand(), "Displays server RAM using a bossbar");
            registrar.register(RamCommands.buildRamCommand(), "Displays server RAM usage in the chat");
            registrar.register(TpsCommands.buildTpsBarCommand(), "Displays server TPS using a bossbar");

            // Server dependant
            registrar.register(RegionCommands.buildRegionBarCommand(), "Displays region TPS using a bossbar");
            registrar.register(WorldCommands.buildWorldBarCommand(), "Displays world MSPT using a bossbar");

            // Plugin dependant
            registrar.register(PapiCommands.buildPapiBarCommand(), "Displays a customized bossbar with PlaceholderAPI placeholders");
        });

    }

}
