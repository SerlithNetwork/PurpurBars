package net.serlith.purpur.commands;

import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.data.DataStorage;
import net.serlith.purpur.tasks.RamBarTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RamBarCommand extends Command implements PluginIdentifiableCommand {

    private final PurpurBars plugin;
    private final List<String> empty = List.of();

    public RamBarCommand(PurpurBars plugin) {
        super("rambar");
        this.plugin = plugin;

        this.setPermission("purpurbars.monitor.ram");
        this.setUsage("/rambar");
        this.setDescription("Displays server RAM using a bossbar");

        this.permissionMessage(plugin.getPrefix().append(RootConfig.MESSAGES._NO_PERMISSION));
        this.plugin.getServer().getCommandMap().register(this.plugin.getNamespace(), this);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(this.plugin.getPrefix().append(RootConfig.MESSAGES._NOT_PLAYER));
            return false;
        }
        RamBarTask.getInstance(this.plugin).togglePlayer(player);
        return true;
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
