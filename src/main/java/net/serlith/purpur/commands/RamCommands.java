package net.serlith.purpur.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.tasks.stats.RamBarTask;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
@UtilityClass
public class RamCommands {

    public LiteralCommandNode<CommandSourceStack> buildRamBarCommand() {
        return Commands.literal("rambar")
                .requires(s -> s.getExecutor() instanceof Player player && player.hasPermission("purpurbars.monitor.ram"))
                .executes(ctx -> {

                    Player player = (Player) ctx.getSource().getExecutor();
                    if (player == null) {
                        throw new IllegalStateException("Command executor cannot be null");
                    }

                    RamBarTask.getInstance().togglePlayer(player);

                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

    public LiteralCommandNode<CommandSourceStack> buildRamCommand() {
        return Commands.literal("ram")
                .requires(s -> s.getSender().hasPermission("purpurbars.check.ram"))
                .executes(ctx -> {

                    CommandSender sender = ctx.getSource().getSender();
                    RamBarTask ramTask = RamBarTask.getInstance();
                    RootConfig.FORMAT.RAM.OUTPUT.stream().map(i -> MiniMessage.miniMessage().deserialize(i,
                            Placeholder.component("allocated", ramTask.format(ramTask.getAllocated())),
                            Placeholder.component("used", ramTask.format(ramTask.getUsed())),
                            Placeholder.component("xmx", ramTask.format(ramTask.getXmx())),
                            Placeholder.component("xms", ramTask.format(ramTask.getXms())),
                            Placeholder.component("bar", RamCommands.createProgressBar(ramTask.getUsed(), ramTask.getXmx())),
                            Placeholder.parsed("percent", "%d%%".formatted((int) (ramTask.getPercent() * 100)))
                    )).forEach(sender::sendMessage);

                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

    private Component createProgressBar(long used, long max) {
        long usedLength = (RootConfig.FORMAT.RAM.USAGE_BAR.LENGTH * used) / max;
        long unusedLength = RootConfig.FORMAT.RAM.USAGE_BAR.LENGTH - usedLength;

        return Component.text(RootConfig.FORMAT.RAM.USAGE_BAR.CHARS.START, RootConfig.FORMAT.RAM.USAGE_BAR.COLOR._BORDER)
                .append(Component.text(RootConfig.FORMAT.RAM.USAGE_BAR.CHARS.BAR.repeat((int) usedLength), RootConfig.FORMAT.RAM.USAGE_BAR.COLOR._USED))
                .append(Component.text(RootConfig.FORMAT.RAM.USAGE_BAR.CHARS.BAR.repeat((int) unusedLength), RootConfig.FORMAT.RAM.USAGE_BAR.COLOR._UNUSED))
                .append(Component.text(RootConfig.FORMAT.RAM.USAGE_BAR.CHARS.END, RootConfig.FORMAT.RAM.USAGE_BAR.COLOR._BORDER));
    }

}
