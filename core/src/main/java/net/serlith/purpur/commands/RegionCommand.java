package net.serlith.purpur.commands;

import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RegionConfig;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.tasks.region.RegionBarTask;
import net.serlith.purpur.tasks.region.RegionFollowBarTask;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RegionCommand extends Command implements PluginIdentifiableCommand {

    private final PurpurBars plugin;
    private final List<String> empty = List.of();

    public RegionCommand(PurpurBars plugin) {
        super("regionbar");
        this.plugin = plugin;

        this.setPermission("purpurbars.monitor.region");
        this.setUsage("/regionbar [player]");
        this.setDescription("Displays region TPS using a bossbar");

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

        if (args.length > 1) {
            sender.sendMessage(this.plugin.getPrefix().append(RootConfig.MESSAGES._NOT_FOUND));
            return false;
        }

        if (args.length == 1) {
            String playerName = args[0];
            RegionBarTask task = this.plugin.getBarsTask().getRegionBarTask(playerName);
            if (task == null) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(plugin.getPrefix().append(RegionConfig.MESSAGES._PLAYER_DOES_NOT_EXIST));
                    return false;
                }
                task = new RegionBarTask(this.plugin, target);
                this.plugin.getBarsTask().addRegionTask(args[0], task);
            }
            task.togglePlayer(player);
            return true;
        }

        RegionFollowBarTask.getInstance().togglePlayer(player);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String @NotNull [] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.startsWith(args[0])).toList();
        }
        return this.empty;
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return this.plugin;
    }

}
