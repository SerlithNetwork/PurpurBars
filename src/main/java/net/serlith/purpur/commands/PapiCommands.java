package net.serlith.purpur.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.experimental.UtilityClass;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.commands.argument.PapiArgument;
import net.serlith.purpur.configs.PapiConfig;
import net.serlith.purpur.tasks.custom.PapiBarTask;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
@UtilityClass
@SuppressWarnings("UnstableApiUsage")
public class PapiCommands {

    public LiteralCommandNode<CommandSourceStack> buildPapiBarCommand() {
        return Commands.literal("papibar")
                .requires(s -> s.getExecutor() instanceof Player player && player.hasPermission("purpurbars.monitor.papi"))
                .then(Commands.argument("bar", new PapiArgument())
                        .executes(ctx -> {

                            CommandSender sender = ctx.getSource().getSender();
                            Player player = (Player) ctx.getSource().getExecutor();
                            if (player == null) {
                                throw new IllegalStateException("Command executor cannot be null");
                            }

                            String bar = ctx.getArgument("bar", String.class);
                            PapiBarTask task = PurpurBars.getInstance().getBarsTask().getPapiBarTask(bar);
                            if (task == null) {
                                sender.sendMessage(PapiConfig.MESSAGES.BAR_DOES_NOT_EXIST.getComponent());
                                return Command.SINGLE_SUCCESS;
                            }

                            task.togglePlayer(player);

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }

}
