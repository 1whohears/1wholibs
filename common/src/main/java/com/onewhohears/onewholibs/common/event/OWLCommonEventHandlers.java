package com.onewhohears.onewholibs.common.event;

import com.mojang.brigadier.CommandDispatcher;
import com.onewhohears.onewholibs.common.command.TestIngredientStackCommand;
import com.onewhohears.onewholibs.common.command.TestPresetCommand;
import com.onewhohears.onewholibs.common.core.DistantRayCastManager;
import com.onewhohears.onewholibs.common.core.FutureRunManager;
import com.onewhohears.onewholibs.common.core.SimulatedEntityManager;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetReloadListener;
import com.onewhohears.onewholibs.data.jsonpreset.test.TestPresets;
import com.onewhohears.onewholibs.util.UtilSync;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collections;
import java.util.List;

public class OWLCommonEventHandlers {

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register(OWLCommonEventHandlers::onPlayerJoin);
        CommandRegistrationEvent.EVENT.register(OWLCommonEventHandlers::registerCommands);
        OWLEvents.GET_JSON_PRESET_LISTENERS.register(OWLCommonEventHandlers::registerPresetListeners);
        LifecycleEvent.SETUP.register(OWLCommonEventHandlers::onSetup);
        TickEvent.SERVER_PRE.register(OWLCommonEventHandlers::onServerTickPre);
        TickEvent.SERVER_POST.register(OWLCommonEventHandlers::onServerTickPost);
        LifecycleEvent.SERVER_STARTING.register(OWLCommonEventHandlers::onServerStarting);
        LifecycleEvent.SERVER_STOPPING.register(OWLCommonEventHandlers::onServerStopping);
    }

    private static void onServerStopping(MinecraftServer server) {
        SimulatedEntityManager.get().serverStop(server);
    }

    private static void onSetup() {
        OWLEvents.registerAllJsonPresetReloadListeners();
    }

    private static void onServerStarting(MinecraftServer server) {
        SimulatedEntityManager.init();
        FutureRunManager.init();
    }

    public static void onServerTickPre(MinecraftServer server) {
        SimulatedEntityManager.get().tickPre(server);
        DistantRayCastManager.onServerTick();
        FutureRunManager.tick(server);
    }

    public static void onServerTickPost(MinecraftServer server) {
        SimulatedEntityManager.get().tickPost(server);
    }

    public static void registerPresetListeners(List<JsonPresetReloadListener<?>> listeners) {
        listeners.add(TestPresets.get());
    }

    public static void onPlayerJoin(ServerPlayer player) {
        UtilSync.syncPresets(Collections.singleton(player));
        UtilSync.syncGameRules(Collections.singleton(player), player.getServer());
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher,
                                        CommandBuildContext context,
                                        Commands.CommandSelection selection) {
        new TestPresetCommand(dispatcher);
        new TestIngredientStackCommand(dispatcher, context);
    }

}
