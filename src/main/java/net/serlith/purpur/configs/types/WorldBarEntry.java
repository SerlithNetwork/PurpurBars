package net.serlith.purpur.configs.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class WorldBarEntry {

    @Getter
    private final String world;

    @Getter @Setter
    private List<UUID> players;

}
