package net.serlith.purpur

import net.kyori.adventure.text.minimessage.MiniMessage
import net.serlith.purpur.commands.MainCommand
import net.serlith.purpur.commands.RamBarCommand
import net.serlith.purpur.commands.TpsBarCommand
import net.serlith.purpur.configs.MainConfigManager
import net.serlith.purpur.tasks.BossBarTask
import org.bukkit.plugin.java.JavaPlugin

class PurpurBars : JavaPlugin() {

    lateinit var mainConfigManager: MainConfigManager
    val prefix = "<gray>[<gradient:#429fff:#d621ff>PurpurBars</gradient>]<gray> "

    companion object {
        lateinit var self: PurpurBars
    }

    override fun onLoad() {
        self = this
    }

    override fun onEnable() {
        this.mainConfigManager = MainConfigManager(this)

        MainCommand(this)
        RamBarCommand(this)
        TpsBarCommand(this)

        BossBarTask.startAll()
        this.printBanner()
    }

    private fun printBanner() {
        arrayOf(
            MiniMessage.miniMessage().deserialize("<gradient:#429fff:#954aeb>     .+------------.+ </gradient>    <light_purple> _____                             ____                  </light_purple>"),
            MiniMessage.miniMessage().deserialize("<gradient:#3ba5ff:#9c52ff>   .'     /      .' | </gradient>    <light_purple>|  __ \\                           |  _ \\                 </light_purple>"),
            MiniMessage.miniMessage().deserialize("<gradient:#2db1ff:#8c60ff>  +------------+'   | </gradient>    <light_purple>| |__) |   _ _ __ _ __  _   _ _ __| |_) | __ _ _ __ ___  </light_purple>"),
            MiniMessage.miniMessage().deserialize("<gradient:#2eb0ff:#8f5eff>  |     |      |    | </gradient>    <light_purple>|  ___/ | | | '__| '_ \\| | | | '__|  _ < / _` | '__/ __| </light_purple>"),
            MiniMessage.miniMessage().deserialize("<gradient:#459cff:#aa47ff>  |     |      |  _-| </gradient>    <light_purple>| |   | |_| | |  | |_) | |_| | |  | |_) | (_| | |  \\__ \\ </light_purple>"),
            MiniMessage.miniMessage().deserialize("<gradient:#5c89ff:#ba3aff>  |------------|-'  | </gradient>    <light_purple>|_|    \\__,_|_|  | .__/ \\__,_|_|  |____/ \\__,_|_|  |___/ </light_purple>"),
            MiniMessage.miniMessage().deserialize("<gradient:#598bff:#c033ff>  |     |      |   .+ </gradient>    <light_purple>                 |_|</light_purple>"),
            MiniMessage.miniMessage().deserialize("<gradient:#6780ff:#ca2bff>  |     |      | .'   </gradient>    <light_purple>                                        by Biquaternions</light_purple>"),
            MiniMessage.miniMessage().deserialize("<gradient:#7375ff:#d621ff>  +------------+'     </gradient>"),
        ).forEach { this.server.sendMessage(it) }
    }

    override fun onDisable() {
        BossBarTask.stopAll()
    }
}
