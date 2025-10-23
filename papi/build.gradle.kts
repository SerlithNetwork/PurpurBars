plugins {
    id("java")
}

group = "net.serlith.purpur"
version = "3.0-SNAPSHOT"

dependencies {
    compileOnly(project(":core"))
    compileOnly("me.clip:placeholderapi:2.11.6")
}
