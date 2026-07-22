package net.serlith.purpur.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.experimental.UtilityClass;
import net.serlith.purpur.tasks.stats.TpsBarTask;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
@UtilityClass
public class TpsCommands {

    public LiteralCommandNode<CommandSourceStack> buildTpsBarCommand() {
        return Commands.literal("tpsbar")
                .requires(s -> s.getExecutor() instanceof Player player && player.hasPermission("purpurbars.monitor.tps"))
                .executes(ctx -> {

                    Player player = (Player) ctx.getSource().getExecutor();
                    if (player == null) {
                        throw new IllegalStateException("Command executor cannot be null");
                    }

                    TpsBarTask.getInstance().togglePlayer(player);

                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

}
