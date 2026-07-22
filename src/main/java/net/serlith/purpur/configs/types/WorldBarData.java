package net.serlith.purpur.configs.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class WorldBarData {

    @Getter
    private final NamespacedKey world;

    @Getter @Setter
    private List<UUID> players;

}
