package net.serlith.purpur.commands;

import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.tasks.CompassBarTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CompassCommand extends Command implements PluginIdentifiableCommand {

    private final PurpurBars plugin;
    private final List<String> empty = List.of();

    public CompassCommand(PurpurBars plugin) {
        super("compass");
        this.plugin = plugin;

        this.setPermission("purpurbars.monitor.compass");
        this.setUsage("/compass");
        this.setDescription("Displays a compass in hand using a bossbar");

        this.permissionMessage(plugin.getPrefix().append(RootConfig.MESSAGES._NO_PERMISSION));
        this.plugin.getServer().getCommandMap().register(this.plugin.getNamespace(), this);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(this.plugin.getPrefix().append(RootConfig.MESSAGES._NOT_PLAYER));
            return false;
        }

        CompassBarTask.getInstance(this.plugin).togglePlayer(player);
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
