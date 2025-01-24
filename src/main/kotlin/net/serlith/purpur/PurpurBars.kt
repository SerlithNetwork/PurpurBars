package net.serlith.purpur

import net.serlith.purpur.commands.MainCommand
import net.serlith.purpur.commands.RamBarCommand
import net.serlith.purpur.commands.TpsBarCommand
import net.serlith.purpur.configs.MainConfigManager
import net.serlith.purpur.tasks.BossBarTask
import org.bukkit.plugin.java.JavaPlugin

class PurpurBars : JavaPlugin() {

    lateinit var mainConfigManager: MainConfigManager

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
    }

    override fun onDisable() {
        BossBarTask.stopAll()
    }
}
