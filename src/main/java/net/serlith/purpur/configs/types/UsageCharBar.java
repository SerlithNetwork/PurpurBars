package net.serlith.purpur.configs.types;

import de.bsommerfeld.jshepherd.annotation.Key;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UsageCharBar {

    @Key("bar")
    public String bar;

    @Key("start")
    public String start;

    @Key("end")
    public String end;

}
