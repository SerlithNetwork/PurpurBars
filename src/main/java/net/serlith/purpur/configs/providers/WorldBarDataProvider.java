package net.serlith.purpur.configs.providers;

import net.j4c0b3y.api.config.provider.TypeProvider;
import net.j4c0b3y.api.config.provider.context.LoadContext;
import net.j4c0b3y.api.config.provider.context.SaveContext;
import net.serlith.purpur.configs.types.WorldBarData;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

import java.util.*;

@NullMarked
public class WorldBarDataProvider implements TypeProvider<WorldBarData> {

    @Override
    @SuppressWarnings("unchecked")
    public WorldBarData load(LoadContext context) {
        if (context.getObject() instanceof Map<?,?> object) {
            String worldString = (String) object.get("world");
            List<UUID> playerUuids = ((List<String>) object.get("players")).stream().map(UUID::fromString).toList();

            NamespacedKey worldKey = null;
            String[] splits = worldString.split(":");
            if (splits.length == 1) {
                String split = splits[0];
                if (!split.isBlank()) {
                    worldKey = NamespacedKey.minecraft(splits[0]);
                }
            }

            if (splits.length == 2) {
                String namespace = splits[0];
                String key = splits[1];
                if (!namespace.isBlank() && !key.isBlank()) {
                    worldKey = new NamespacedKey(namespace, key);
                }
            }

            if (worldKey == null) {
                throw new IllegalStateException(String.format("The key is invalid, it does not follow the 'namespace:key' structure: Key(%s)", worldString));
            }

            return new WorldBarData(worldKey, playerUuids);
        }
        throw new IllegalStateException("Cannot serialize world bar entry");
    }

    @Override
    public Object save(SaveContext<WorldBarData> context) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("world", context.getObject().getWorld().asString());
        map.put("players", context.getObject().getPlayers().stream().map(UUID::toString).toList());
        return map;
    }

}
