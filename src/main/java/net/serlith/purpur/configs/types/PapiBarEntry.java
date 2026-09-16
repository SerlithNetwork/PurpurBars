package net.serlith.purpur.configs.types;

import de.bsommerfeld.jshepherd.annotation.Key;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.kyori.adventure.bossbar.BossBar;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PapiBarEntry {

    @Key("name")
    public String name;

    @Key("title")
    public String title;

    @Key("min")
    public String min;

    @Key("value")
    public String value;

    @Key("max")
    public String max;

    @Key("update-interval")
    public int updateInterval;

    @Key("overlay")
    public BossBar.Overlay overlay;

    @Key("color-low")
    public BossBar.Color colorLow;

    @Key("color-middle")
    public BossBar.Color colorMiddle;

    @Key("color-high")
    public BossBar.Color colorHigh;

    @Key("bound-middle")
    public double boundMiddle;

    @Key("bound-high")
    public double boundHigh;

}
