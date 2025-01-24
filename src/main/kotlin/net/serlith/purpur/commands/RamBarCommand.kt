package net.serlith.purpur.commands

import net.kyori.adventure.text.minimessage.MiniMessage
import net.serlith.purpur.PurpurBars
import net.serlith.purpur.tasks.RamBarTask
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class RamBarCommand (
    private val plugin: PurpurBars,
) : Command("rambar"), PluginIdentifiableCommand {
    private val empty = emptyList<String>()

    init {
        this.permission = "purpurbars.monitor.ram"
        this.usage = "/rambar"
        this.description = "Displays server RAM using a bossbar"

        this.permissionMessage(MiniMessage.miniMessage().deserialize(this.plugin.prefix + this.plugin.mainConfigManager.messages.noPermission))
        this.plugin.server.commandMap.register("serlith", this)
    }

    override fun execute(
        sender: CommandSender,
        commandLabel: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(this.plugin.prefix + this.plugin.mainConfigManager.messages.notPlayer))
            return false
        }
        RamBarTask.instance(PurpurBars.self).togglePlayer(sender)
        return true
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): List<String?> {
        return this.empty
    }

    override fun getPlugin(): Plugin = this.plugin
}