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
    public BossBar.Color good;

    @Key("medium")
    public BossBar.Color medium;

    @Key("low")
    public BossBar.Color low;

}
