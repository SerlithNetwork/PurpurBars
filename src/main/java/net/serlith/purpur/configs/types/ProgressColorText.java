package net.serlith.purpur.configs.types;

import de.bsommerfeld.jshepherd.annotation.Comment;
import de.bsommerfeld.jshepherd.annotation.Key;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProgressColorText {

    @Comment("\uD83D\uDD25 Text format to use when usage of a resource is good")
    @Key("good")
    public String good;

    @Comment("\uD83D\uDD25 Text format to use when usage of a resource is medium")
    @Key("medium")
    public String medium;

    @Comment("\uD83D\uDD25 Text format to use when usage of a resource is low")
    @Key("low")
    public String low;

}
