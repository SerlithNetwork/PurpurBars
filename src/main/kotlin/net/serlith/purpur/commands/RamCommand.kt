package net.serlith.purpur.commands

import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.serlith.purpur.PurpurBars
import net.serlith.purpur.tasks.RamBarTask
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.plugin.Plugin

class RamCommand (
    private val plugin: PurpurBars,
) : Command("ram"), PluginIdentifiableCommand {
    private val empty = emptyList<String>()

    init {
        this.permission = "purpurbars.check.ram"
        this.usage = "/ram"
        this.description = "Displays server RAM usage in the chat"

        this.permissionMessage(MiniMessage.miniMessage().deserialize(this.plugin.prefix + this.plugin.mainConfigManager.messages.noPermission))
        this.plugin.server.commandMap.register(this.plugin.namespace, this)
    }

    override fun execute(
        sender: CommandSender,
        commandLabel: String,
        args: Array<out String>?
    ): Boolean {
        val rambar = RamBarTask.instance(this.plugin)
        this.plugin.mainConfigManager.ram.output.map {
            MiniMessage.miniMessage().deserialize(it,
                Placeholder.component("allocated", rambar.format(rambar.allocated)),
                Placeholder.component("used", rambar.format(rambar.used)),
                Placeholder.component("xmx", rambar.format(rambar.xmx)),
                Placeholder.component("xms", rambar.format(rambar.xms)),
                Placeholder.component("bar", this.createProgressBar(rambar.used, rambar.xmx)),
                Placeholder.unparsed("percent", "${(rambar.percent * 100).toInt()}%"),
            )
        }.forEach { sender.sendMessage(it) }
        return true
    }

    private fun createProgressBar(used: Long, max: Long): ComponentLike {
        val usedLength = (this.plugin.mainConfigManager.ram.length * used) / max
        val unusedLength = this.plugin.mainConfigManager.ram.length - usedLength

        val charBar = this.plugin.mainConfigManager.ram.char.bar
        val charStart = this.plugin.mainConfigManager.ram.char.start
        val charEnd = this.plugin.mainConfigManager.ram.char.end

        val usedColor = this.plugin.mainConfigManager.ram.color.used
        val unusedColor = this.plugin.mainConfigManager.ram.color.unused
        val usedBar = "<color:${usedColor}>${charBar.repeat(usedLength.toInt())}</color:${usedColor}>"
        val unusedBar = "<color:${unusedColor}>${charBar.repeat(unusedLength.toInt())}</color:${unusedColor}>"

        val borderColor = this.plugin.mainConfigManager.ram.color.border
        return MiniMessage.miniMessage().deserialize(
            "<color:${borderColor}>$charStart</color:${borderColor}> ${usedBar}${unusedBar} <color:${borderColor}>$charEnd</color:${borderColor}>"
        )
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>?): List<String?> {
        return this.empty
    }

    override fun getPlugin(): Plugin = this.plugin

}