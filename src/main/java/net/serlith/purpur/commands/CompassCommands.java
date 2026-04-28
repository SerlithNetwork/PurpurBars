package net.serlith.purpur.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.experimental.UtilityClass;
import net.serlith.purpur.tasks.stats.CompassBarTask;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
@UtilityClass
@SuppressWarnings("UnstableApiUsage")
public class CompassCommands {

    public LiteralCommandNode<CommandSourceStack> buildCompassCommand() {
        return Commands.literal("compass")
                .requires(s -> s.getExecutor() instanceof Player player && player.hasPermission("purpurbars.monitor.compass")) // TODO: Check if executor is enough, or should I use sender
                .executes(ctx -> {

                    Player player = (Player) ctx.getSource().getExecutor();
                    if (player == null) {
                        throw new IllegalStateException("Command executor cannot be null");
                    }

                    CompassBarTask.getInstance().togglePlayer(player);

                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

}
