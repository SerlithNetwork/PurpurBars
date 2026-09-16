package net.serlith.purpur.configs.types;

import de.bsommerfeld.jshepherd.annotation.Key;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProgressColorText {

    @Key("good")
    public String good;

    @Key("medium")
    public String medium;

    @Key("low")
    public String low;

}
