package net.serlith.purpur.configs.providers;

import net.j4c0b3y.api.config.provider.TypeProvider;
import net.j4c0b3y.api.config.provider.context.LoadContext;
import net.j4c0b3y.api.config.provider.context.SaveContext;
import net.kyori.adventure.bossbar.BossBar;
import net.serlith.purpur.configs.types.PapiBarEntry;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class PapiBarEntryProvider implements TypeProvider<PapiBarEntry> {

    @Override
    public @Nullable PapiBarEntry load(LoadContext context) {
        if (context.getObject() instanceof Map<?,?> object) {
            if (object.get("color") instanceof Map<?,?> color && object.get("bound") instanceof Map<?,?> bound) {
                return new PapiBarEntry(
                        (String) object.get("name"),
                        (String) object.get("title"),
                        (String) object.get("min"),
                        (String) object.get("value"),
                        (String) object.get("max"),
                        (int) object.get("update-interval"),
                        BossBar.Overlay.valueOf((String) object.get("overlay")),
                        BossBar.Color.valueOf((String) color.get("low")),
                        BossBar.Color.valueOf((String) color.get("middle")),
                        BossBar.Color.valueOf((String) color.get("high")),
                        (double) bound.get("middle"),
                        (double) bound.get("high")
                );
            }
            throw new IllegalStateException("Cannot serialize PAPI bar colors/bounds");
        }
        throw new IllegalStateException("Cannot serialize PAPI bar entry");
    }

    @Override
    public @Nullable Object save(SaveContext<PapiBarEntry> context) {
        Map<String, Object> colors = new LinkedHashMap<>();
        Map<String, Object> bounds = new LinkedHashMap<>();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", context.getObject().name());
        map.put("title", context.getObject().title());
        map.put("min", context.getObject().min());
        map.put("value", context.getObject().value());
        map.put("max", context.getObject().max());
        map.put("update-interval", context.getObject().updateInterval());
        map.put("overlay", context.getObject().overlay().name().toUpperCase());

        colors.put("low", context.getObject().colorLow().name().toUpperCase());
        colors.put("middle", context.getObject().colorMiddle().name().toUpperCase());
        colors.put("high", context.getObject().colorHigh().name().toUpperCase());
        map.put("color", colors);

        bounds.put("middle", context.getObject().boundMiddle());
        bounds.put("high", context.getObject().boundHigh());
        map.put("bound", bounds);

        return map;
    }

}
