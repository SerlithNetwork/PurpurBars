package net.serlith.purpur.configs.types;

import de.bsommerfeld.jshepherd.annotation.Comment;
import de.bsommerfeld.jshepherd.annotation.Key;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.kyori.adventure.bossbar.BossBar;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PapiBarEntry {

    @Comment("\uD83D\uDD25 Unique to identify this bar")
    @Key("name")
    public String name;

    @Comment("\uD83D\uDD25 Text to display in the bar")
    @Key("title")
    public String title;

    @Comment("\uD83D\uDD25 Text representing the minimum value of a bar")
    @Key("min")
    public String min;

    @Comment("\uD83D\uDD25 Text representing the current value of a bar, usually a placeholder")
    @Key("value")
    public String value;

    @Comment("\uD83D\uDD25 Text representing the maximum value of a bar")
    @Key("max")
    public String max;

    @Comment("\uD83D\uDD25 Delay (in ticks) between bar updates")
    @Key("update-interval")
    public int updateInterval;

    @Comment("\uD83D\uDD25 Possible overlays: https://jd.papermc.io/adventure/5.2.0/net.kyori.adventure.api/net/kyori/adventure/bossbar/BossBar.Overlay.html")
    @Key("overlay")
    public BossBar.Overlay overlay;

    @Comment("\uD83D\uDD25 Possible colors: https://jd.papermc.io/adventure/5.2.0/net.kyori.adventure.api/net/kyori/adventure/bossbar/BossBar.Color.html")
    @Key("color-low")
    public BossBar.Color colorLow;

    @Comment("\uD83D\uDD25 Possible colors: https://jd.papermc.io/adventure/5.2.0/net.kyori.adventure.api/net/kyori/adventure/bossbar/BossBar.Color.html")
    @Key("color-middle")
    public BossBar.Color colorMiddle;

    @Comment("\uD83D\uDD25 Possible colors: https://jd.papermc.io/adventure/5.2.0/net.kyori.adventure.api/net/kyori/adventure/bossbar/BossBar.Color.html")
    @Key("color-high")
    public BossBar.Color colorHigh;

    @Comment("\uD83D\uDD25 Threshold value when the progress of a bar is considered medium progress")
    @Key("bound-middle")
    public double boundMiddle;

    @Comment("\uD83D\uDD25 Threshold value when the progress of a bar is considered high progress")
    @Key("bound-high")
    public double boundHigh;

}
