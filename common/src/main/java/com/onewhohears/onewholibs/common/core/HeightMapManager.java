package com.onewhohears.onewholibs.common.core;

import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.util.UtilFile;
import com.onewhohears.onewholibs.util.math.UtilGeometry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HeightMapManager {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int RESOLUTION = 4;

    /**
     * the short[] represents a 4 x 4 grid of heights
     */
    private static final Map<ResourceKey<Level>, Map<Long,short[]>> HEIGHT_MAP = new HashMap<>();

    public static boolean isCrossed(ResourceKey<Level> dimension, Vec3 position, boolean goingDown) {
        Map<Long,short[]> map = HEIGHT_MAP.get(dimension);
        if (map == null) return false;
        BlockPos blockPos = UtilGeometry.toBlockPos(position);
        ChunkPos chunkPos = new ChunkPos(blockPos);
        long chunkId = chunkPos.toLong();
        short[] heights = map.get(chunkId);
        if (heights == null) return false;
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();
        int relX = blockPos.getX() - minX;
        int relZ = blockPos.getZ() - minZ;
        int x = relX / RESOLUTION;
        int z = relZ / RESOLUTION;
        short height = heights[x * z];
        return (goingDown && position.y <= height) || (!goingDown && position.y > height);
    }

    public static void onChunkLoad(@NotNull ChunkAccess chunk, @Nullable ServerLevel level) {
        if (level == null) return;
        Map<Long, short[]> map = HEIGHT_MAP.computeIfAbsent(level.dimension(), k -> new HashMap<>());
        short[] heights = new short[RESOLUTION * RESOLUTION];
        for (int x = 0; x < RESOLUTION; ++x) {
            for (int z = 0; z < RESOLUTION; ++z) {
                heights[x * z] = (short) chunk.getHeight(Heightmap.Types.MOTION_BLOCKING,
                        x * RESOLUTION, z * RESOLUTION);
            }
        }
        map.put(chunk.getPos().toLong(), heights);
        if (map.size() % 1000 == 0) LOGGER.info("height map size {}", map.size());
    }

    public static void save(@NotNull ServerLevel level) {
        Map<Long, short[]> maps = HEIGHT_MAP.computeIfAbsent(level.dimension(), k -> new HashMap<>());
        Map<Long, Map<Long, short[]>> regions = new HashMap<>();
        for (Map.Entry<Long, short[]> entry : maps.entrySet()) {
            long chunkKey = entry.getKey();
            int cx = ChunkPos.getX(chunkKey);
            int cz = ChunkPos.getZ(chunkKey);
            long regionKey = (((long)(cx >> 5)) << 32) | ((cz >> 5) & 0xffffffffL);
            regions.computeIfAbsent(regionKey, k -> new HashMap<>())
                    .put(chunkKey, entry.getValue());
        }
        for (Map.Entry<Long, Map<Long, short[]>> region : regions.entrySet()) {
            Path file = resolveRegionPath(level.dimension(), region.getKey(), level.getServer());
            try {
                saveRegion(file, region.getValue());
            } catch (IOException e) {
                LOGGER.error("Failed to save height map region file {} {} {}",
                        level.dimension().location(), region.getKey(), e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static Path resolveRegionPath(ResourceKey<Level> dimension, Long key, MinecraftServer server) {
        return resolveRegionPath(dimension, key+"", server);
    }

    private static Path resolveRegionPath(ResourceKey<Level> dimension, String key, MinecraftServer server) {
        ResourceLocation drl = dimension.location();
        return UtilFile.getWorldFolder(server).resolve("data/heightmaps/"
                +drl.getNamespace()+"/"+drl.getPath()+"/"+key+".hmap");
    }

    public static void load(@NotNull ServerLevel level) {
        Map<Long, short[]> map = HEIGHT_MAP.computeIfAbsent(level.dimension(), k -> new HashMap<>());
        map.clear();
        ResourceLocation drl = level.dimension().location();
        Set<String> regions = UtilFile.getFileNamesEndingWithInGamePath(
                "data/heightmaps/"+drl.getNamespace()+"/"+drl.getPath()+"/",
                ".hmap", level.getServer());
        for (String region : regions) {
            Path regionPath = resolveRegionPath(level.dimension(), region, level.getServer());
            try {
                loadRegion(regionPath, map);
            } catch (IOException e) {
                LOGGER.error("Failed to load height map region file {} {} {}",
                        level.dimension().location(), region, e.getMessage());
                e.printStackTrace();
            }
        }
        LOGGER.info("{} HEIGHT MAP SIZE AFTER LOAD = {}", drl, map.size());
    }

    private static void saveRegion(Path file, Map<Long, short[]> chunkMap) throws IOException {
        Path p = Path.of(file.toString()).getParent().normalize();
        new File(p.toUri()).mkdirs();
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(file)))) {
            out.writeInt(1);
            out.writeInt(chunkMap.size());
            for (Map.Entry<Long, short[]> entry : chunkMap.entrySet()) {
                out.writeLong(entry.getKey());
                short[] data = entry.getValue();
                for (short s : data) {
                    out.writeShort(s);
                }
            }
        }
    }

    private static void loadRegion(Path file, Map<Long, short[]> map) throws IOException {
        if (!Files.exists(file)) return;
        int res2 = RESOLUTION * RESOLUTION;
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
            int version = in.readInt();
            int count = in.readInt();
            for (int i = 0; i < count; i++) {
                long chunkPos = in.readLong();
                short[] data = new short[res2];
                for (int j = 0; j < res2; j++) {
                    data[j] = in.readShort();
                }
                map.put(chunkPos, data);
            }
        }
    }

    public static void unload(@NotNull ServerLevel level) {
        HEIGHT_MAP.remove(level.dimension());
    }
}
