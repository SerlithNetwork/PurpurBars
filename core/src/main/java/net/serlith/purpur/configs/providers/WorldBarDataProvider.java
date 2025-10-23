package net.serlith.purpur.configs.providers;

import net.j4c0b3y.api.config.provider.TypeProvider;
import net.j4c0b3y.api.config.provider.context.LoadContext;
import net.j4c0b3y.api.config.provider.context.SaveContext;
import net.serlith.purpur.configs.types.WorldBarData;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WorldBarDataProvider implements TypeProvider<WorldBarData> {

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable WorldBarData load(LoadContext context) {
        if (context.getObject() instanceof Map<?,?> object) {
            return new WorldBarData((String) object.get("world"), ((List<String>) object.get("players")).stream().map(UUID::fromString).toList());
        }
        throw new IllegalStateException("Cannot serialize world bar entry");
    }

    @Override
    public @Nullable Object save(SaveContext<WorldBarData> context) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("world", context.getObject().getWorld());
        map.put("players", context.getObject().getPlayers().stream().map(UUID::toString).toList());
        return map;
    }

}
