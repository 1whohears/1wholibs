package com.onewhohears.onewholibs.common.core;

import com.mojang.datafixers.util.Pair;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class FutureRunManager {

    private static final IntObjectMap<Pair<CanServerRun, ServerRunnable>> FUTURE_RUNS = new IntObjectHashMap<>();
    private static final Set<Integer> FOR_REMOVAL = new HashSet<>();
    private static int RUN_COUNTER = 0;

    public static void addTimedFutureRunnable(@NotNull MinecraftServer server, int ticks,
                                              @NotNull FutureRunManager.ServerRunnable run) {
        long goalTime = server.getTickCount() + ticks;
        addFutureRunnable(serv -> serv.getTickCount() >= goalTime, run);
    }

    public static void addFutureRunnable(@NotNull FutureRunManager.CanServerRun test,
                                         @NotNull FutureRunManager.ServerRunnable run) {
        FUTURE_RUNS.put(++RUN_COUNTER, Pair.of(test, run));
    }

    public static void tick(@NotNull MinecraftServer server) {
        FUTURE_RUNS.forEach((id, pair) -> {
            if (FOR_REMOVAL.contains(id)) return;
            server.execute(() -> {
                if (FOR_REMOVAL.contains(id)) return;
                if (pair.getFirst().test(server)) {
                    server.execute(() -> {
                        if (FOR_REMOVAL.contains(id)) return;
                        pair.getSecond().run(server);
                        FOR_REMOVAL.add(id);
                    });
                }
            });
        });
        FOR_REMOVAL.forEach(FUTURE_RUNS::remove);
        if (FUTURE_RUNS.isEmpty()) FOR_REMOVAL.clear();
    }

    public interface CanServerRun {
        boolean test(@NotNull MinecraftServer server);
    }

    public interface ServerRunnable {
        void run(@NotNull MinecraftServer server);
    }

}
