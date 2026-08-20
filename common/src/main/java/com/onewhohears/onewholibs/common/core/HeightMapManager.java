package com.onewhohears.onewholibs.common.core;

import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.util.UtilEntity;
import com.onewhohears.onewholibs.util.UtilFile;
import com.onewhohears.onewholibs.util.math.UtilGeometry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class HeightMapManager {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int VERSION = 7;
    private static final int RESOLUTION = 4;
    private static final int RESOLUTION_WIDTH = 16 / RESOLUTION;

    /**
     * the short[] represents a 4 x 4 grid of heights
     */
    private static final Map<ResourceKey<Level>, Map<Long,short[]>> HEIGHT_MAP = new HashMap<>();

    public static short getHeight(@NotNull ResourceKey<Level> dimension, @NotNull Vec3 pos) {
        Map<Long,short[]> map = HEIGHT_MAP.get(dimension);
        if (map == null) return Short.MIN_VALUE;
        BlockPos blockPos = UtilGeometry.toBlockPos(pos);
        ChunkPos chunkPos = new ChunkPos(blockPos);
        long chunkId = chunkPos.toLong();
        short[] heights = map.get(chunkId);
        if (heights == null) return Short.MIN_VALUE;
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();
        int relX = blockPos.getX() - minX;
        int relZ = blockPos.getZ() - minZ;
        int x = relX / RESOLUTION;
        int z = relZ / RESOLUTION;
        return heights[getHeightIndex(x, z)];
    }

    public static int getHeightIndex(int localX, int localZ) {
        return localX * RESOLUTION + localZ;
    }

    public static int massHeightMapLoadWB(@NotNull ServerLevel level) {
        WorldBorder border = level.getWorldBorder();
        if (border.getSize() >= border.getAbsoluteMaxSize()) return -1;
        return massHeightMapLoad(level, border.getCenterX(), border.getCenterZ(), border.getSize());
    }

    public static int massHeightMapLoad(@NotNull ServerLevel level, double centerX, double centerZ, double diameter) {
        double radius = diameter / 2;
        int minX = SectionPos.blockToSectionCoord(centerX - radius);
        int minZ = SectionPos.blockToSectionCoord(centerZ - radius);
        int maxX = SectionPos.blockToSectionCoord(centerX + radius);
        int maxZ = SectionPos.blockToSectionCoord(centerZ + radius);
        int k = 0;
        MinecraftServer server = level.getServer();
        Map<Long, short[]> map = HEIGHT_MAP.computeIfAbsent(level.dimension(), l -> new HashMap<>());
        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                long chunkPos = ChunkPos.asLong(x, z);
                if (map.containsKey(chunkPos)) continue;
                final int cx = x, cz = z;
                FutureRunManager.addTimedFutureRunnable(server, k, srv -> {
                    onChunkLoad(level.getChunk(cx, cz), level);
                    if (cx == maxX && cx == maxZ) {
                        LOGGER.info("FINISHED HEIGHT MAP GEN");
                    }
                });
                k++;
            }
        }
        LOGGER.info("GENERATING HEIGHT MAP FOR {} NEW CHUNKS. ETA {} MINUTES", k, k/20/60);
        return k;
    }

    public static void onChunkLoad(@NotNull ChunkAccess chunk, @Nullable ServerLevel level) {
        if (level == null) return;
        Map<Long, short[]> map = HEIGHT_MAP.computeIfAbsent(level.dimension(), k -> new HashMap<>());
        short[] heights = new short[RESOLUTION * RESOLUTION];
        for (int x = 0; x < RESOLUTION; ++x) {
            for (int z = 0; z < RESOLUTION; ++z) {
                int maxHeight = level.getMinBuildHeight();
                for (int xr = 0; xr < RESOLUTION_WIDTH; ++xr) {
                    for (int zr = 0; zr < RESOLUTION_WIDTH; ++zr) {
                        int height = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING,
                                x * RESOLUTION + xr, z * RESOLUTION + zr);
                        if (height > maxHeight) maxHeight = height;
                    }
                }
                heights[getHeightIndex(x, z)] = (short) maxHeight;
                // TODO instead of only saving the max height, need to save the gaps of air between.
            }
        }
        map.put(chunk.getPos().toLong(), heights);
        if (map.size() % 1000 == 0) LOGGER.info("HEIGHT MAP SIZE {}", map.size());
    }

    public static void onBlockUpdate(@NotNull BlockPos pos, @NotNull ServerLevel level, boolean place) {
        Map<Long, short[]> map = HEIGHT_MAP.computeIfAbsent(level.dimension(), k -> new HashMap<>());
        ChunkPos chunkPos = new ChunkPos(pos);
        int chunkMinX = chunkPos.getMinBlockX();
        int chunkMinZ = chunkPos.getMinBlockZ();
        int xr = Mth.floor((pos.getX() - chunkMinX) / (float) RESOLUTION);
        int zr = Mth.floor((pos.getZ() - chunkMinZ) / (float) RESOLUTION);
        short[] heights = map.get(chunkPos.toLong());
        int index = getHeightIndex(xr, zr);
        if (place) {
            if (pos.getY() > heights[index]) heights[index] = (short) pos.getY();
            return;
        }
        int maxHeight = level.getMinBuildHeight();
        int nextMaxHeight = level.getMinBuildHeight();
        boolean maxHeightRepeat = false;
        ChunkAccess chunk = level.getChunk(chunkPos.x, chunkPos.z);
        for (int x = 0; x < RESOLUTION_WIDTH; ++x) {
            for (int z = 0; z < RESOLUTION_WIDTH; ++z) {
                int xc = x + xr * RESOLUTION + chunkMinX;
                int zc = z + zr * RESOLUTION + chunkMinZ;
                int height = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING, xc, zc);
                if (height == maxHeight) maxHeightRepeat = true;
                if (height > maxHeight) {
                    nextMaxHeight = maxHeight;
                    maxHeight = height;
                    maxHeightRepeat = false;
                } else if (height > nextMaxHeight) {
                    nextMaxHeight = height;
                }
            }
        }
        if (!maxHeightRepeat && maxHeight == pos.getY()) {
            while (--maxHeight > nextMaxHeight) {
                if (UtilEntity.blocksMotion(chunk.getBlockState(pos.atY(maxHeight)))) {
                    break;
                }
            }
        }
        heights[index] = (short) maxHeight;
    }

    public static boolean generateHeightmapImage(@NotNull MinecraftServer server, @NotNull ServerLevel level,
                                                 @NotNull Consumer<String> debug) {
        int minBuildHeight = level.getMinBuildHeight();
        return generateHeightmapImage(server, level, minBuildHeight, debug);
    }

    public static boolean generateHeightmapImage(@NotNull MinecraftServer server, @NotNull ServerLevel level,
                                                 int minBuildHeight, @NotNull Consumer<String> debug) {
        int maxBuildHeight = level.getMaxBuildHeight();
        return generateHeightmapImage(server, level, minBuildHeight, maxBuildHeight, debug);
    }

    public static boolean generateHeightmapImage(@NotNull MinecraftServer server, @NotNull ServerLevel level,
                                                 int minBuildHeight, int maxBuildHeight,
                                                 @NotNull Consumer<String> debug) {
        Map<Long, short[]> chunkHeightmaps = HEIGHT_MAP.computeIfAbsent(level.dimension(), k -> new HashMap<>());
        if (chunkHeightmaps.isEmpty()) {
            debug.accept("No Heightmap Loaded in this Dimension");
            return false;
        }

        int minChunkX = Integer.MAX_VALUE;
        int minChunkZ = Integer.MAX_VALUE;
        int maxChunkX = Integer.MIN_VALUE;
        int maxChunkZ = Integer.MIN_VALUE;

        for (long packedPos : chunkHeightmaps.keySet()) {
            ChunkPos pos = new ChunkPos(packedPos);
            minChunkX = Math.min(minChunkX, pos.x);
            minChunkZ = Math.min(minChunkZ, pos.z);
            maxChunkX = Math.max(maxChunkX, pos.x);
            maxChunkZ = Math.max(maxChunkZ, pos.z);
        }

        int chunkWidth = maxChunkX - minChunkX + 1;
        int chunkHeight = maxChunkZ - minChunkZ + 1;
        int imageWidth = chunkWidth * RESOLUTION;
        int imageHeight = chunkHeight * RESOLUTION;
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        int buildRange = maxBuildHeight - minBuildHeight;
        int heightsLength = RESOLUTION * RESOLUTION;

        for (Map.Entry<Long, short[]> entry : chunkHeightmaps.entrySet()) {
            ChunkPos chunkPos = new ChunkPos(entry.getKey());
            short[] heights = entry.getValue();
            if (heights == null || heights.length != heightsLength) continue;
            int chunkPixelX = (chunkPos.x - minChunkX) * RESOLUTION;
            int chunkPixelZ = (chunkPos.z - minChunkZ) * RESOLUTION;
            for (int localX = 0; localX < RESOLUTION; ++localX) {
                for (int localZ = 0; localZ < RESOLUTION; ++localZ) {
                    int index = getHeightIndex(localX, localZ);
                    int height = heights[index];
                    float normalized = (float)(height - minBuildHeight) / (float)buildRange;
                    normalized = Math.max(0.0f, Math.min(1.0f, normalized));
                    int gray = (int)(normalized * 255.0f);
                    int rgb = (gray << 16) | (gray << 8) | gray;
                    int pixelX = chunkPixelX + localX;
                    int pixelY = chunkPixelZ + localZ;
                    image.setRGB(pixelX, pixelY, rgb);
                }
            }
        }
        ResourceLocation drl = level.dimension().location();
        String dateTime = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        Path outputPath = UtilFile.getWorldFolder(server).resolve("data/heightmap_images/"
                +drl.getNamespace()+"/"+drl.getPath()+"/"
                +drl.getNamespace()+"_"+drl.getPath()+"_heightmap_"+dateTime+".png");
        File outputFile = outputPath.toFile();
        outputFile.getParentFile().mkdirs();
        try {
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        debug.accept("Successfully created height map image at "+outputPath);
        return true;
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
        LOGGER.info("{} HEIGHT MAP CHUNKS AFTER LOAD = {}", drl, map.size());
    }

    private static void saveRegion(Path file, Map<Long, short[]> chunkMap) throws IOException {
        Path p = Path.of(file.toString()).getParent().normalize();
        new File(p.toUri()).mkdirs();
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(file)))) {
            out.writeInt(VERSION);
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
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
            int version = in.readInt();
            if (version == VERSION) {
                int res2 = RESOLUTION * RESOLUTION;
                int count = in.readInt();
                for (int i = 0; i < count; i++) {
                    long chunkPos = in.readLong();
                    short[] data = new short[res2];
                    for (int j = 0; j < res2; j++) {
                        data[j] = in.readShort();
                    }
                    map.put(chunkPos, data);
                }
                in.close();
                return;
            } else if (version == 6) {
                int res2 = RESOLUTION * RESOLUTION;
                int count = in.readInt();
                for (int i = 0; i < count; i++) {
                    long chunkPos = in.readLong();
                    short[] data = new short[res2];
                    for (int j = 0; j < res2; j++) {
                        data[j] = in.readShort();
                    }
                    map.put(chunkPos, data);
                }
                in.close();
                return;
            }
            in.close();
            LOGGER.error("Height Map File {} Unsupported Version. File = {}, Expected = {}. Canceling Read.",
                    file.getFileName(), version, VERSION);
        }
    }

    public static void unload(@NotNull ServerLevel level) {
        HEIGHT_MAP.remove(level.dimension());
    }
}
