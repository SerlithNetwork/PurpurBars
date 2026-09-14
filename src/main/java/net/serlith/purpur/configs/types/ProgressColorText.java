package net.serlith.purpur.configs.types;

import de.bsommerfeld.jshepherd.annotation.Key;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProgressColorText {

    @Key("good")
    private String good;

    @Key("medium")
    private String medium;

    @Key("low")
    private String low;

}
