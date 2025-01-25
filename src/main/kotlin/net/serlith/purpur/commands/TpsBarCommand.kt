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
        this.permission = "purpurbars.monitor.tps"
        this.usage = "/tpsbar"
        this.description = "Displays server TPS using a bossbar"

        this.permissionMessage(MiniMessage.miniMessage().deserialize(this.plugin.prefix + this.plugin.mainConfigManager.messages.noPermission))
        this.plugin.server.commandMap.register(this.plugin.namespace, this)
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
        TpsBarTask.instance(PurpurBars.self).togglePlayer(sender)
        return true
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): List<String?> {
        return this.empty
    }

    override fun getPlugin(): Plugin = this.plugin
}