package com.onewhohears.onewholibs.integration.distanthorizons;

import com.mojang.logging.LogUtils;
import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.interfaces.data.IDhApiTerrainDataCache;
import com.seibel.distanthorizons.api.objects.DhApiResult;
import com.seibel.distanthorizons.api.objects.data.DhApiRaycastResult;
import com.seibel.distanthorizons.api.objects.data.DhApiTerrainDataPoint;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class DHUtil {

    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * CLIENT ONLY!
     * DO NOT CALL DIRECTLY USE
     * {@link com.onewhohears.onewholibs.OWLDependencySafety#distantHorizonsRaycast(Vec3, Vec3, int)}
     */
    public static boolean raycast(Vec3 start, Vec3 end, int maxDistance) {
        Vec3 diff = end.subtract(start);
        maxDistance = Math.min(maxDistance, (int) diff.length());
        DhApiResult<DhApiRaycastResult> result = DhApi.Delayed.terrainRepo.raycast(
                DhApi.Delayed.worldProxy.getSinglePlayerLevel(),
                start.x, start.y, start.z, (float) diff.x, (float )diff.y, (float) diff.z,
                maxDistance, getTerrainCache());
        return result.payload == null;
    }

    private static IDhApiTerrainDataCache TERRAIN_CACHE = null;

    private static IDhApiTerrainDataCache getTerrainCache() {
        if (TERRAIN_CACHE == null) {
            TERRAIN_CACHE = DhApi.Delayed.terrainRepo.createSoftCache();
        }
        return TERRAIN_CACHE;
    }
}
