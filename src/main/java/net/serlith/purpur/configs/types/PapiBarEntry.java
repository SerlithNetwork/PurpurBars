package net.serlith.purpur.configs.types;

import net.kyori.adventure.bossbar.BossBar;

public record PapiBarEntry(
        String name,
        String title,
        String min,
        String value,
        String max,
        int updateInterval,
        BossBar.Overlay overlay,
        BossBar.Color colorLow,
        BossBar.Color colorMiddle,
        BossBar.Color colorHigh,
        double boundMiddle,
        double boundHigh
) {}
