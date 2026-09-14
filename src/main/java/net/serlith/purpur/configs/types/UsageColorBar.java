package net.serlith.purpur.configs.types;

import de.bsommerfeld.jshepherd.annotation.Key;
import de.bsommerfeld.jshepherd.annotation.PostInject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.format.TextColor;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings({"FieldCanBeLocal", "unused", "NotNullFieldNotInitialized"})
public class UsageColorBar {

    @Key("used")
    private String usedString;
    public transient TextColor used = TextColor.color(0xF0, 0x99, 0xEE);

    @Key("unused")
    private String unusedString;
    public transient TextColor unused = TextColor.color(0xF0, 0x99, 0xEE);

    @Key("border")
    private String borderString;
    public transient TextColor border = TextColor.color(0xF0, 0x99, 0xEE);

    public UsageColorBar(final String used, final String unused, final String border) {
        this.usedString = used;
        this.unusedString = unused;
        this.borderString = border;
    }

    @PostInject
    public void convert() {
        this.used = Objects.requireNonNull(TextColor.fromHexString(this.usedString));
        this.unused = Objects.requireNonNull(TextColor.fromHexString(this.unusedString));
        this.border = Objects.requireNonNull(TextColor.fromHexString(this.borderString));
    }

}
