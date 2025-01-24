package net.serlith.purpur.commands

import net.kyori.adventure.text.minimessage.MiniMessage
import net.serlith.purpur.PurpurBars
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.plugin.Plugin

class MainCommand (
    private val plugin: PurpurBars,
) : Command("purpurbars"), PluginIdentifiableCommand {
    private val empty = emptyList<String>()
    private val options = listOf("return")

    init {
        this.plugin.server.commandMap.register("serlith", this)
    }

    override fun execute(
        sender: CommandSender,
        commandLabel: String,
        args: Array<out String>
    ): Boolean {
        if (!sender.hasPermission("purpurbars.admin")) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>You have no permission to run this command"))
            return false
        }
        this.plugin.mainConfigManager.reload()
        return true
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): List<String?> {
        if (!sender.hasPermission("purpurbars.admin")) return this.empty
        if (args.isEmpty()) return this.empty
        if (args.size == 1) return this.options
        return this.empty
    }

    override fun getPlugin(): Plugin = this.plugin
}