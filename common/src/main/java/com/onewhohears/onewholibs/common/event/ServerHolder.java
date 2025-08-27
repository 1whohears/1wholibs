package com.onewhohears.onewholibs.common.event;

import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public class ServerHolder {
    @Nullable
    private static MinecraftServer server;

    @Nullable
    public static MinecraftServer get() {
        return server;
    }

    public static void init() {
        LifecycleEvent.SERVER_STARTING.register(s -> server = s);
        LifecycleEvent.SERVER_STOPPED.register(s -> server = null);
    }
}
