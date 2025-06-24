package net.serlith.purpur.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.tasks.RamBarTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RamCommand extends Command implements PluginIdentifiableCommand {

    private final PurpurBars plugin;
    private final List<String> empty = List.of();

    public RamCommand(PurpurBars plugin) {
        super("ram");
        this.plugin = plugin;

        this.setPermission("purpurbars.check.ram");
        this.setUsage("/ram");
        this.setDescription("Displays server RAM usage in the chat");

        this.permissionMessage(plugin.getPrefix().append(RootConfig.MESSAGES._NO_PERMISSION));
        this.plugin.getServer().getCommandMap().register(this.plugin.getNamespace(), this);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        RamBarTask ramTask = RamBarTask.getInstance(this.plugin);
        RootConfig.FORMAT.RAM.OUTPUT.stream().map(i -> MiniMessage.miniMessage().deserialize(i,
                Placeholder.component("allocated", ramTask.format(ramTask.getAllocated())),
                Placeholder.component("used", ramTask.format(ramTask.getUsed())),
                Placeholder.component("xmx", ramTask.format(ramTask.getXmx())),
                Placeholder.component("xms", ramTask.format(ramTask.getXms())),
                Placeholder.component("bar", this.createProgressBar(ramTask.getUsed(), ramTask.getXmx())),
                Placeholder.unparsed("percent", "%d%%".formatted((int) ramTask.getPercent() * 100))
                )).forEach(sender::sendMessage);
        return true;
    }

    private Component createProgressBar(long used, long max) {
        long usedLength = (RootConfig.FORMAT.RAM.USAGE_BAR.LENGTH * used) / max;
        long unusedLength = RootConfig.FORMAT.RAM.USAGE_BAR.LENGTH - usedLength;

        return Component.text(RootConfig.FORMAT.RAM.USAGE_BAR.CHARS.START, RootConfig.FORMAT.RAM.USAGE_BAR.COLOR._BORDER)
                .append(Component.text(RootConfig.FORMAT.RAM.USAGE_BAR.CHARS.BAR.repeat((int) usedLength), RootConfig.FORMAT.RAM.USAGE_BAR.COLOR._USED))
                .append(Component.text(RootConfig.FORMAT.RAM.USAGE_BAR.CHARS.BAR.repeat((int) unusedLength), RootConfig.FORMAT.RAM.USAGE_BAR.COLOR._UNUSED))
                .append(Component.text(RootConfig.FORMAT.RAM.USAGE_BAR.CHARS.END, RootConfig.FORMAT.RAM.USAGE_BAR.COLOR._BORDER));
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
        return this.empty;
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return this.plugin;
    }
}
