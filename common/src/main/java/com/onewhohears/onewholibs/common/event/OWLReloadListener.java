package com.onewhohears.onewholibs.common.event;

import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.util.UtilSync;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class OWLReloadListener implements PreparableReloadListener {

    static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public @NotNull CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager,
                                                   ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler,
                                                   Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.runAsync(() -> {
            MinecraftServer server = ServerHolder.get();
            if (server == null) return;
            LOGGER.info("RELOAD LISTENER FIRING");
            UtilSync.syncPresets(server);
            UtilSync.syncGameRules(server);
        }, backgroundExecutor).thenCompose(barrier::wait);
    }

    public static void register() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new OWLReloadListener());
    }
}
