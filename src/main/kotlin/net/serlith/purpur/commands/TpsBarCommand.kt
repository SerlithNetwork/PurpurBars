package net.serlith.purpur.commands

import net.kyori.adventure.text.minimessage.MiniMessage
import net.serlith.purpur.PurpurBars
import net.serlith.purpur.tasks.TpsBarTask
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class TpsBarCommand (
    private val plugin: PurpurBars,
) : Command("tpsbar"), PluginIdentifiableCommand {
    private val empty = emptyList<String>()

    init {
        this.plugin.server.commandMap.register("serlith", this)
    }

    override fun execute(
        sender: CommandSender,
        commandLabel: String,
        args: Array<out String>
    ): Boolean {
        if (!sender.hasPermission("purpurbars.monitor.tps")) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>You have no permission to run this command"))
            return false
        }
        if (sender !is Player) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>This command can only be used by a player"))
            return false
        }
        TpsBarTask.instance(PurpurBars.self).togglePlayer(sender)
        return true
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): List<String?> {
        return this.empty
    }

    override fun getPlugin(): Plugin = this.plugin
}