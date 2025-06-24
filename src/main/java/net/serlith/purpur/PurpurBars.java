package net.serlith.purpur;

import lombok.Getter;
import net.j4c0b3y.api.config.ConfigHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.serlith.purpur.commands.MainCommand;
import net.serlith.purpur.commands.RamBarCommand;
import net.serlith.purpur.commands.RamCommand;
import net.serlith.purpur.commands.TpsBarCommand;
import net.serlith.purpur.configs.RootConfig;
import net.serlith.purpur.data.DataStorage;
import net.serlith.purpur.listeners.PlayerListener;
import net.serlith.purpur.tasks.BossBarTask;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.stream.Stream;

public final class PurpurBars extends JavaPlugin {

    @Getter
    private static PurpurBars instance;

    @Getter
    private final String namespace = "purpurbars";
    @Getter
    private ConfigHandler configHandler;
    @Getter
    private Component prefix;
    @Getter
    private File storageFolder;

    @Override
    public void onLoad() {
        instance = this;
        this.configHandler = new ConfigHandler();
        this.prefix = MiniMessage.miniMessage().deserialize("<gray>[<gradient:#429fff:#d621ff>PurpurBars</gradient>]<gray> ");
        this.storageFolder = new File(getDataFolder(), "storage");
    }

    @Override
    public void onEnable() {
        new RootConfig(this).load();
        new DataStorage(this).load();
        new Metrics(this, 24547);

        new MainCommand(this);
        new TpsBarCommand(this);
        new RamBarCommand(this);
        new RamCommand(this);
        new PlayerListener(this);

        BossBarTask.startAll();
        this.printBanner();
    }

    @Override
    public void onDisable() {
        DataStorage.INSTANCE.save();
        BossBarTask.stopAll();
    }


    private void printBanner() {
        Stream.of(
                "<gradient:#3ba5ff:#9c52ff>    .+--------.+  </gradient>",
                        "<gradient:#2db1ff:#8c60ff>   .'   /    .' | </gradient>",
                        "<gradient:#2eb0ff:#8f5eff>  +--------+' | | </gradient>    <light_purple>▄▖          ▄       </light_purple>",
                        "<gradient:#459cff:#aa47ff>  |   |    |  .'| </gradient>    <light_purple>▙▌▌▌▛▘▛▌▌▌▛▘▙▘▀▌▛▘▛▘</light_purple>",
                        "<gradient:#5c89ff:#ba3aff>  |--------|' | | </gradient>    <light_purple>▌ ▙▌▌ ▙▌▙▌▌ ▙▘█▌▌ ▄▌</light_purple>",
                        "<gradient:#598bff:#c033ff>  |   |    |  .'  </gradient>    <light_purple>      ▌             </light_purple>",
                        "<gradient:#6780ff:#ca2bff>  +--------+'     </gradient>"
                )
                .map(MiniMessage.miniMessage()::deserialize)
                .forEach(this.getServer()::sendMessage);
    }

}
