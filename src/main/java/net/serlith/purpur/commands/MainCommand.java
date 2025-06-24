package net.serlith.purpur.commands;

import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RootConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainCommand extends Command implements PluginIdentifiableCommand {

    private final PurpurBars plugin;
    private final List<String> empty = List.of();
    private final List<String> options = List.of("reload");

    public MainCommand(PurpurBars plugin) {
        super("purpurbars");
        this.plugin = plugin;

        this.setPermission("purpurbars.admin");
        this.setUsage("/purpurbars [reload]");
        this.setDescription("Main PurpurBars administration command");

        this.permissionMessage(plugin.getPrefix().append(RootConfig.MESSAGES._NO_PERMISSION));
        this.plugin.getServer().getCommandMap().register(this.plugin.getNamespace(), this);
    }

    @Override
    public boolean execute(
            @NotNull CommandSender sender,
            @NotNull String commandLabel,
            @NotNull String[] args
    ) {

        if (args.length != 1 && !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(this.plugin.getPrefix().append(RootConfig.MESSAGES._NOT_FOUND));
            return false;
        }

        try {
            RootConfig.INSTANCE.load();
        } catch (Exception e) {
            sender.sendMessage(this.plugin.getPrefix().append(RootConfig.MESSAGES._FAILED_RELOAD));
            return false;
        }

        sender.sendMessage(this.plugin.getPrefix().append(RootConfig.MESSAGES._SUCCESSFUL_RELOAD));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) return this.options;
        return this.empty;
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return this.plugin;
    }
}
