package com.onewhohears.onewholibs.entity;

import com.onewhohears.onewholibs.common.command.CustomGameRules;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetReloadListener;
import com.onewhohears.onewholibs.data.jsonpreset.test.TestPresetStats;
import com.onewhohears.onewholibs.data.jsonpreset.test.TestPresets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class TestEntity extends JsonPresetEntity<TestPresetStats> implements SimulatedEntity {

    private long lastServerTick;

    public TestEntity(EntityType<?> entityType, Level level) {
        super(entityType, level, "test0");
    }

    @Override
    public void tick() {
        SimulatedEntity.super.onVanillaTick();
        super.tick();
        move(MoverType.SELF, getDeltaMovement());
    }

    @Override
    public void onAlwaysTickPre(@NotNull MinecraftServer server) {
        if (tickCount%20==0) System.out.println("ALWAYS TICK "+tickCount+" "+lastServerTick+" "+this);
        int speed = getWorld().getGameRules().getInt(CustomGameRules.TEST_ENTITY_SPEED);
        if (speed <= 0) {
            setDeltaMovement(Vec3.ZERO);
            return;
        }
        Player player = getWorld().getNearestPlayer(this, 10E6);
        if (player == null) {
            setDeltaMovement(Vec3.ZERO);
            return;
        }
        Vec3 move = player.position().subtract(position()).normalize().scale(speed * 0.05);
        setDeltaMovement(move);
    }

    @Override
    public void onSimulatedTick(@NotNull MinecraftServer server) {
        if (tickCount%20==0) System.out.println("SIMULATED TICK "+tickCount+" "+lastServerTick+" "+this);
        move(MoverType.SELF, getDeltaMovement());
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public @NotNull JsonPresetReloadListener<TestPresetStats> getPresets() {
        return TestPresets.get();
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {

    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {

    }

    @Override
    public long getLastServerTick() {
        return lastServerTick;
    }

    @Override
    public void setLastServerTick(long tick) {
        lastServerTick = tick;
    }
}
