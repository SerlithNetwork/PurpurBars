package net.serlith.purpur.configs.types;

import de.bsommerfeld.jshepherd.annotation.Comment;
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

    @Comment("\uD83D\uDD25 Color to use when usage of a resource is good")
    @Key("good")
    public BossBar.Color good;

    @Comment("\uD83D\uDD25 Color to use when usage of a resource is medium")
    @Key("medium")
    public BossBar.Color medium;

    @Comment("\uD83D\uDD25 Color to use when usage of a resource is low")
    @Key("low")
    public BossBar.Color low;

}
