package com.onewhohears.onewholibs.common.event;

import com.mojang.brigadier.CommandDispatcher;
import com.onewhohears.onewholibs.common.command.TestIngredientStackCommand;
import com.onewhohears.onewholibs.common.command.TestPresetCommand;
import com.onewhohears.onewholibs.common.core.DistantRayCastManager;
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
        LifecycleEvent.SETUP.register(OWLEvents::registerAllJsonPresetReloadListeners);
        TickEvent.SERVER_PRE.register(OWLCommonEventHandlers::onServerTickPre);
    }

    public static void onServerTickPre(MinecraftServer server) {
        DistantRayCastManager.onServerTick();
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
