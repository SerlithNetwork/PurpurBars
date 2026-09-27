package net.serlith.purpur.concurrent;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class PurpurBarsThread extends Thread {

    public PurpurBarsThread(final Runnable runnable) {
        super(runnable);
    }

}
