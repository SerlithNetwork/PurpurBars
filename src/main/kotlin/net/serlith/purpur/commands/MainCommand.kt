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
    private val options = listOf("reload")

    init {
        this.permission = "purpurbars.admin"
        this.usage = "/purpurbars [reload]"
        this.description = "Main PurpurBars administration command"

        this.permissionMessage(MiniMessage.miniMessage().deserialize("${this.plugin.prefix}<red>You have no permission to run this command"))
        this.plugin.server.commandMap.register("serlith", this)
    }

    override fun execute(
        sender: CommandSender,
        commandLabel: String,
        args: Array<out String>
    ): Boolean {
        if ("reload" !in args) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("${this.plugin.prefix}<red>Command not found"))
            return false
        }
        try {
            this.plugin.mainConfigManager.reload()
        } catch (_: Exception) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("${this.plugin.prefix}<red>Failed to load configuration!"))
            return false
        }
        sender.sendMessage(MiniMessage.miniMessage().deserialize("${this.plugin.prefix}<white>Configuration reloaded!"))
        return true
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): List<String?> {
        if (args.isEmpty()) return this.empty
        if (args.size == 1) return this.options
        return this.empty
    }

    override fun getPlugin(): Plugin = this.plugin
}