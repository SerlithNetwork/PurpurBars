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
import net.serlith.purpur.tasks.region.compat.CompatRegionBarTask;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
@UtilityClass
public class RegionCommands {

    public LiteralCommandNode<CommandSourceStack> buildRegionBarCommand() {
        return Commands.literal("regionbar")
                .requires(s -> PurpurBars.getInstance().isSupportsFoliaMetrics() && s.getExecutor() instanceof Player player && player.hasPermission("purpurbars.monitor.region"))
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

                            PlayerSelectorArgumentResolver playerResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                            Player target = playerResolver.resolve(ctx.getSource()).getFirst();

                            PurpurBars plugin = PurpurBars.getInstance();
                            RegionBarTask task = plugin.getBarsTask().getRegionBarTask(target);
                            if (task == null) {
                                task = plugin.isSupportsFoliaMetrics() ? new RegionBarTask(plugin, target) : new CompatRegionBarTask(plugin, target);
                                plugin.getBarsTask().addRegionTask(target, task);
                            }
                            task.togglePlayer(player);

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .build();
    }

}
