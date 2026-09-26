package net.serlith.purpur.util;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record Tuple3<T, U, V>(T first, U second, V third) {
}
