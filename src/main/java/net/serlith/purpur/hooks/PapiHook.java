package net.serlith.purpur.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.serlith.purpur.PurpurBars;
import net.serlith.purpur.tasks.stats.RamBarTask;
import net.serlith.purpur.tasks.stats.TpsBarTask;
import net.serlith.purpur.tasks.world.WorldBarTask;
import net.serlith.purpur.util.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public class PapiHook extends PlaceholderExpansion {

    private final PurpurBars plugin;
    private final boolean supportsPWT;
    private final boolean supportsFolia;

    public PapiHook(PurpurBars plugin) {
        this.plugin = plugin;
        this.supportsPWT = this.plugin.getGetWorldAverageTickTime() != null;
        this.supportsFolia = this.plugin.getGetRegionTPS() != null;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "purpurbars";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Biquaternions";
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public @NotNull String getVersion() {
        return this.plugin.getPluginMeta().getVersion();
    }

    @Override
    public String onRequest(@Nullable OfflinePlayer player, @NotNull String params) {
        return switch (params) {
            case "tps" -> "%.2f".formatted(TpsBarTask.getInstance().getTps());
            case "mspt" -> "%.2f".formatted(TpsBarTask.getInstance().getMspt());

            case "tps_min" -> "%.2f".formatted(TpsBarTask.getInstance().getTpsMin());
            case "tps_max" -> "%.2f".formatted(TpsBarTask.getInstance().getTpsMax());
            case "tps_50ile" -> "%.2f".formatted(TpsBarTask.getInstance().getTps50Percentile());
            case "tps_95ile" -> "%.2f".formatted(TpsBarTask.getInstance().getTps95Percentile());

            case "mspt_min" -> "%.2f".formatted(TpsBarTask.getInstance().getMsptMin());
            case "mspt_max" -> "%.2f".formatted(TpsBarTask.getInstance().getMsptMax());
            case "mspt_50ile" -> "%.2f".formatted(TpsBarTask.getInstance().getMspt50Percentile());
            case "mspt_95ile" -> "%.2f".formatted(TpsBarTask.getInstance().getMspt95Percentile());

            case "ram_allocated" -> Utils.formatBytes(RamBarTask.getInstance().getAllocated());
            case "ram_used" -> Utils.formatBytes(RamBarTask.getInstance().getUsed());
            case "ram_xmx" -> Utils.formatBytes(RamBarTask.getInstance().getXmx());
            case "ram_xms" -> Utils.formatBytes(RamBarTask.getInstance().getXms());
            case "ram_percent" -> "%d%%".formatted((int) (RamBarTask.getInstance().getPercent() * 100));

            default -> {
                String[] args = params.split("_", 2);
                if (args.length != 2) yield null;

                if (this.supportsPWT && args[1].equalsIgnoreCase("mspt")) {
                    WorldBarTask task;
                    if (args[0].equalsIgnoreCase("@") && player instanceof Player onlinePlayer) {
                        task = this.plugin.getBarsTask().getWorldBarTask(onlinePlayer.getWorld().getName());
                    } else {
                        task = this.plugin.getBarsTask().getWorldBarTask(args[0]);
                    }
                    if (task == null) yield null;
                    yield "%.2f".formatted(task.getMspt());
                }

                if (this.supportsFolia && args[0].equalsIgnoreCase("region") && player instanceof Player onlinePlayer) {
                    if (args[1].equalsIgnoreCase("tps")) {
                        double tps = 20.0;
                        try {
                            tps = ((double[]) this.plugin.getGetRegionTPS().invoke(null, onlinePlayer.getLocation()))[0];
                        } catch (IllegalAccessException | InvocationTargetException ignore) {}
                        yield "%.2f".formatted(tps);
                    } else if (args[1].equalsIgnoreCase("mspt")) {
                        double mspt = 0.0;
                        try {
                            mspt = this.plugin.getGetRegionAverageTickTimes() == null ? 0.0 : ((double[]) this.plugin.getGetRegionAverageTickTimes().invoke(null, onlinePlayer.getLocation()))[0];
                        } catch (IllegalAccessException | InvocationTargetException ignore) {}
                        yield "%.2f".formatted(mspt);
                    }
                    yield null;
                }

                yield null;
            }
        };
    }

}
