package net.serlith.purpur.commands;

import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.PapiConfig;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.tasks.custom.PapiBarTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PapiCommand extends Command implements PluginIdentifiableCommand {

    private final PurpurBars plugin;
    private final List<String> empty = List.of();

    public PapiCommand(PurpurBars plugin) {
        super("papibar");
        this.plugin = plugin;

        this.setPermission("purpurbars.monitor.papi");
        this.setUsage("/papibar <bar>");
        this.setDescription("Displays a customized bossbar with PlaceholderAPI placeholders");

        this.permissionMessage(plugin.getPrefix().append(RootConfig.MESSAGES._NO_PERMISSION));
        this.plugin.getServer().getCommandMap().register(this.plugin.getNamespace(), this);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(this.plugin.getPrefix().append(RootConfig.MESSAGES._NOT_PLAYER));
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage(this.plugin.getPrefix().append(RootConfig.MESSAGES._NOT_FOUND));
            return false;
        }

        PapiBarTask task = this.plugin.getBarsTask().getPapiBarTask(args[0]);
        if (task == null) {
            sender.sendMessage(this.plugin.getPrefix().append(PapiConfig.MESSAGES._BAR_DOES_NOT_EXIST));
            return false;
        }

        task.togglePlayer(player);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String @NotNull [] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return PapiConfig.PAPI_BARS_NAMES.stream().filter(s -> s.startsWith(args[0])).toList();
        }
        return this.empty;
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return this.plugin;
    }

}
