package net.serlith.purpur.commands;

import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.configs.WorldConfig;
import net.serlith.purpur.tasks.world.WorldBarTask;
import net.serlith.purpur.tasks.world.WorldFollowBarTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WorldBarCommand extends Command implements PluginIdentifiableCommand {

    private final PurpurBars plugin;
    private final List<String> empty = List.of();

    public WorldBarCommand(PurpurBars plugin) {
        super("worldbar");
        this.plugin = plugin;

        this.setPermission("purpurbars.monitor.world");
        this.setUsage("/worldbar [world]");
        this.setDescription("Displays world MSPT using a bossbar");

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
            String worldName = args[0];
            WorldBarTask task = this.plugin.getBarsTask().getWorldBarTask(worldName);
            if (task == null) {
                sender.sendMessage(this.plugin.getPrefix().append(WorldConfig.MESSAGES._WORLD_DOES_NOT_EXIST));
                return false;
            }
            task.togglePlayer(player);
            return true;
        }

        WorldFollowBarTask.getInstance().togglePlayer(player);

        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String @NotNull [] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return Bukkit.getWorlds().stream().map(World::getName).toList();
        }
        return this.empty;
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return this.plugin;
    }

}
