package com.onewhohears.onewholibs.client.core;

import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.OWLDependencySafety;
import com.onewhohears.onewholibs.common.core.RayCastPerspective;
import com.onewhohears.onewholibs.common.network.toserver.ToServerCanSeePos;
import com.onewhohears.onewholibs.util.UtilEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class DistantRayCastManagerClient {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void handleS2CRayCast(int rayCastId, RayCastPerspective perspective,
                                        int perspectiveEntityId, Vec3 targetPos,
                                        double throWater, double throBlock) {
        Minecraft m = Minecraft.getInstance();
        ClientLevel level = m.level;
        if (level == null) return;
        Entity entity = level.getEntity(perspectiveEntityId);
        if (entity == null) {
            LOGGER.warn("Resieved Ray Cast Packet for an entity that doesn't exist " +
                    "in the client level with id {}", perspectiveEntityId);
            return;
        }
        int renderDistanceBlocks = Math.min(192, m.options.getEffectiveRenderDistance() * 16);
        Vec3 start = entity.getEyePosition();
        boolean result = UtilEntity.isLocalVisionBlocked(level, start,
                targetPos, throWater, throBlock, renderDistanceBlocks);
        if (result) {
            boolean dhResult = OWLDependencySafety.distantHorizonsRaycast(start, targetPos, 2048);
            if (!dhResult) {
                result = false;
            }
        }
        sendRayCastResult(rayCastId, perspective, result);
    }

    public static void sendRayCastResult(int rayCastId, RayCastPerspective perspective, boolean result) {
        new ToServerCanSeePos(rayCastId, perspective, result).sendToServer();
    }

}
