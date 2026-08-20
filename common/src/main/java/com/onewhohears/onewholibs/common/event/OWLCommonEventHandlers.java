package com.onewhohears.onewholibs.common.event;

import com.mojang.brigadier.CommandDispatcher;
import com.onewhohears.onewholibs.common.command.TestIngredientStackCommand;
import com.onewhohears.onewholibs.common.command.TestPresetCommand;
import com.onewhohears.onewholibs.common.command.VisibleCommands;
import com.onewhohears.onewholibs.common.core.DistantVisibleManager;
import com.onewhohears.onewholibs.common.core.FutureRunManager;
import com.onewhohears.onewholibs.common.core.HeightMapManager;
import com.onewhohears.onewholibs.common.core.SimulatedEntityManager;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetReloadListener;
import com.onewhohears.onewholibs.data.jsonpreset.test.TestPresets;
import com.onewhohears.onewholibs.util.UtilSync;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.*;
import dev.architectury.utils.value.IntValue;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;

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
        ChunkEvent.LOAD_DATA.register(OWLCommonEventHandlers::onChunkLoad);
        LifecycleEvent.SERVER_LEVEL_LOAD.register(OWLCommonEventHandlers::onServerLevelLoad);
        LifecycleEvent.SERVER_LEVEL_SAVE.register(OWLCommonEventHandlers::onServerLevelSave);
        LifecycleEvent.SERVER_LEVEL_UNLOAD.register(OWLCommonEventHandlers::onServerLevelUnload);
        BlockEvent.BREAK.register(OWLCommonEventHandlers::onBreakBlock);
        BlockEvent.PLACE.register(OWLCommonEventHandlers::onPlaceBlock);
        BlockEvent.FALLING_LAND.register(OWLCommonEventHandlers::onBlockFallingLand);
    }

    private static void onBlockFallingLand(Level level, BlockPos pos, BlockState fallState, BlockState landOnState,
                                           FallingBlockEntity fallingBlockEntity) {
        if (level.isClientSide()) return;
        HeightMapManager.onBlockUpdate(pos, (ServerLevel) level, true);
    }

    private static EventResult onPlaceBlock(Level level, BlockPos pos, BlockState state, @Nullable Entity entity) {
        if (level.isClientSide()) return EventResult.pass();
        HeightMapManager.onBlockUpdate(pos, (ServerLevel) level, true);
        return EventResult.pass();
    }

    private static EventResult onBreakBlock(Level level, BlockPos pos, BlockState state,
                                            ServerPlayer player, @Nullable IntValue intValue) {
        if (level.isClientSide()) return EventResult.pass();
        HeightMapManager.onBlockUpdate(pos, (ServerLevel) level, false);
        return EventResult.pass();
    }

    private static void onServerLevelSave(ServerLevel level) {
        HeightMapManager.save(level);
    }

    private static void onServerLevelLoad(ServerLevel level) {
        HeightMapManager.load(level);
    }

    private static void onServerLevelUnload(ServerLevel level) {
        HeightMapManager.unload(level);
    }

    private static void onChunkLoad(ChunkAccess chunk, @Nullable ServerLevel level, CompoundTag nbt) {
        HeightMapManager.onChunkLoad(chunk, level);
    }

    private static void onServerStopping(MinecraftServer server) {
        SimulatedEntityManager.get().serverStop(server);
        DistantVisibleManager.onServerStop(server);
    }

    private static void onSetup() {
        OWLEvents.registerAllJsonPresetReloadListeners();
    }

    private static void onServerStarting(MinecraftServer server) {
        SimulatedEntityManager.init();
        FutureRunManager.init();
        DistantVisibleManager.onServerStart(server);
    }

    public static void onServerTickPre(MinecraftServer server) {
        SimulatedEntityManager.get().tickPre(server);
        DistantVisibleManager.onServerTick(server);
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
        new VisibleCommands(dispatcher);
    }

}
