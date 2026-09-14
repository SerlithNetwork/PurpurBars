package net.serlith.purpur.configs.types;

import de.bsommerfeld.jshepherd.annotation.Key;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.kyori.adventure.bossbar.BossBar;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProgressColorBar {

    @Key("good")
    private BossBar.Color good;

    @Key("medium")
    private BossBar.Color medium;

    @Key("low")
    private BossBar.Color low;

}
