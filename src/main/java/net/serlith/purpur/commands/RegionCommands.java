package net.serlith.purpur.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import lombok.experimental.UtilityClass;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.tasks.region.RegionBarTask;
import net.serlith.purpur.tasks.region.RegionFollowBarTask;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
@UtilityClass
public class RegionCommands {

    public LiteralCommandNode<CommandSourceStack> buildRegionBarCommand() {
        return Commands.literal("regionbar")
                .requires(s -> PurpurBars.getInstance().isSupportsFoliaTPS() && s.getExecutor() instanceof Player player && player.hasPermission("purpurbars.monitor.region"))
                .executes(ctx -> {

                    Player player = (Player) ctx.getSource().getExecutor();
                    if (player == null) {
                        throw new IllegalStateException("Command executor cannot be null");
                    }

                    RegionFollowBarTask.getInstance().togglePlayer(player);

                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("player", ArgumentTypes.player())
                        .executes(ctx -> {

                            Player player = (Player) ctx.getSource().getExecutor();
                            if (player == null) {
                                throw new IllegalStateException("Command executor cannot be null");
                            }

                            CommandSender sender = ctx.getSource().getSender();
                            PlayerSelectorArgumentResolver playerResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                            Player target = playerResolver.resolve(ctx.getSource()).getFirst();

                            RegionBarTask task = PurpurBars.getInstance().getBarsTask().getRegionBarTask(target);
                            if (task == null) {
                                task = new RegionBarTask(PurpurBars.getInstance(), target);
                                PurpurBars.getInstance().getBarsTask().addRegionTask(target, task);
                            }
                            task.togglePlayer(player);

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }

}
