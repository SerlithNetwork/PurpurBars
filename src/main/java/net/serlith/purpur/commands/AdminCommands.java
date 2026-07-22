package net.serlith.purpur.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.experimental.UtilityClass;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.PapiConfig;
import net.serlith.purpur.configs.RegionConfig;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.configs.WorldConfig;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;

@NullMarked
@UtilityClass
public class AdminCommands {

    public LiteralCommandNode<CommandSourceStack> buildMainCommand() {
        return Commands.literal("purpurbars")
                .requires(s -> s.getSender().hasPermission("purpurbars.admin"))
                .then(Commands.literal("reload")
                        .executes(ctx -> {

                            CommandSender sender = ctx.getSource().getSender();

                            try {
                                RootConfig.INSTANCE.load();
                                if (WorldConfig.INSTANCE != null) WorldConfig.INSTANCE.load();
                                if (RegionConfig.INSTANCE != null) RegionConfig.INSTANCE.load();
                                if (PapiConfig.INSTANCE != null) PapiConfig.INSTANCE.load();
                                sender.sendMessage(RootConfig.MESSAGES.SUCCESSFUL_RELOAD.getComponent());
                            } catch (Exception e) {
                                sender.sendMessage(RootConfig.MESSAGES.FAILED_RELOAD.getComponent());
                                PurpurBars.getInstance().getSLF4JLogger().error("Failed to reload config", e);
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }

}
