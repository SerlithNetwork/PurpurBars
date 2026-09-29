package net.serlith.purpur.configs.types;

import de.bsommerfeld.jshepherd.annotation.Comment;
import de.bsommerfeld.jshepherd.annotation.Key;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UsageCharBar {

    @Comment("\uD83D\uDD25 Character to use to represent the usage bar")
    @Key("bar")
    public String bar;

    @Comment("\uD83D\uDD25 Character to delimit the start of the bar")
    @Key("start")
    public String start;

    @Comment("\uD83D\uDD25 Character to delimit the end of the bar")
    @Key("end")
    public String end;

}
