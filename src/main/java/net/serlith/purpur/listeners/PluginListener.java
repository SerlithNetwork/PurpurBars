package net.serlith.purpur.listeners;

import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.hooks.PapiHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class PluginListener implements Listener {

    private final PurpurBars plugin;
    private PapiHook hook = null;

    public PluginListener(PurpurBars plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        if (hook == null) {
            this.hook = new PapiHook(this.plugin);
            this.hook.register();
        }
    }

}
