package net.serlith.purpur.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import lombok.experimental.UtilityClass;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.WorldConfig;
import net.serlith.purpur.tasks.world.WorldBarTask;
import net.serlith.purpur.tasks.world.WorldFollowBarTask;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
@UtilityClass
@SuppressWarnings("UnstableApiUsage")
public class WorldCommands {

    public LiteralCommandNode<CommandSourceStack> buildWorldBarCommand() {
        return Commands.literal("worldbar")
                .requires(s -> s.getExecutor() instanceof Player player && player.hasPermission("purpurbars.monitor.world"))
                .executes(ctx -> {

                    Player player = (Player) ctx.getSource().getExecutor();
                    if (player == null) {
                        throw new IllegalStateException("Command executor cannot be null");
                    }

                    WorldFollowBarTask.getInstance().togglePlayer(player);

                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("world", ArgumentTypes.world())
                        .executes(ctx -> {

                            Player player = (Player) ctx.getSource().getExecutor();
                            if (player == null) {
                                throw new IllegalStateException("Command executor cannot be null");
                            }

                            World world = ctx.getArgument("world", World.class);
                            WorldBarTask task = PurpurBars.getInstance().getBarsTask().getWorldBarTask(world);
                            if (task == null) {
                                player.sendMessage(WorldConfig.MESSAGES.WORLD_DOES_NOT_EXIST.getComponent());
                                return Command.SINGLE_SUCCESS;
                            }
                            task.togglePlayer(player);

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }

}
