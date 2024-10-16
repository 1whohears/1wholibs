package com.onewhohears.onewholibs.util;

import java.awt.Color;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.onewhohears.onewholibs.util.math.UtilAngles;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author 1whohears
 */
public class UtilEntity {
	
	public static final Random random = new Random();
	/**
	 * @param level the level blocks should be checked in
	 * @param start_pos the start position of the raycast
	 * @param end_pos the end position of the raycast
	 * @param maxBlockCheckDepth the raycast will not check more than this many blocks for obstruction.
	 *                           if this distance between start_pos and the entity is larger,
	 *                           will check maxBlockCheckDepth/2 blocks after start_pos and
	 *                           maxBlockCheckDepth/2 blocks before the entity. there can be extreme
	 *                           performance issues if this is more than 500 depending on specs.
	 * @param throWater max number of blocks of water the raycast can go through without returning false
	 * @param throBlock max number of solid blocks the raycast can go through without returning false
	 * @return true if there is direct line of sight between pos and the eye position of entity
	 */
	public static boolean canPosSeePos(Level level, Vec3 start_pos, Vec3 end_pos, int maxBlockCheckDepth, double throWater, double throBlock) {
		Vec3 diff = end_pos.subtract(start_pos);
		Vec3 look = diff.normalize();
		double distance = diff.length();
		double[] through = new double[] {throWater, throBlock};
		if (distance <= maxBlockCheckDepth) {
			if (!checkBlocksByRange(level, start_pos, look, (int)distance, through)) return false;
		} else {
			int maxCheckDist = maxBlockCheckDepth / 2;
			if (!checkBlocksByRange(level, start_pos, look, maxCheckDist, through)) return false;
			if (!checkBlocksByRange(level,
					start_pos.add(look.scale(distance-maxCheckDist).subtract(look)),
					look, maxCheckDist, through)) return false;
		}
		return true;
	}

	/**
	 * @param start_pos the start position of the ray cast
	 * @param entity the eye position of this entity is the end point of the raycast
	 * @param maxBlockCheckDepth the raycast will not check more than this many blocks for obstruction.
	 *                           if this distance between start_pos and the entity is larger,
	 *                           will check maxBlockCheckDepth/2 blocks after start_pos and
	 *                           maxBlockCheckDepth/2 blocks before the entity. there can be extreme
	 *                           performance issues if this is more than 500 depending on specs.
	 * @param throWater max number of blocks of water the raycast can go through without returning false
	 * @param throBlock max number of solid blocks the raycast can go through without returning false
	 * @return true if there is direct line of sight between pos and the eye position of entity
	 */
	public static boolean canPosSeeEntity(Vec3 start_pos, Entity entity, int maxBlockCheckDepth, double throWater, double throBlock) {
		return canPosSeePos(entity.level(), start_pos, entity.getEyePosition(), maxBlockCheckDepth, throWater, throBlock);
	}

	/**
	 * @param entity1 the eye position is the start position of the raycast
	 * @param entity2 the eye position of this entity is the end point of the raycast
	 * @param maxBlockCheckDepth the raycast will not check more than this many blocks for obstruction.
	 *                           if this distance between start_pos and the entity is larger,
	 *                           will check maxBlockCheckDepth/2 blocks after start_pos and
	 *                           maxBlockCheckDepth/2 blocks before the entity. there can be extreme
	 *                           performance issues if this is more than 500 depending on specs.
	 * @return true if there is direct line of sight between pos and the eye position of entity
	 */
	public static boolean canEntitySeeEntity(Entity entity1, Entity entity2, int maxBlockCheckDepth) {
		return canEntitySeeEntity(entity1, entity2, maxBlockCheckDepth, 0, 0);
	}

	/**
	 * @param entity1 the eye position is the start position of the raycast
	 * @param entity2 the eye position of this entity is the end point of the raycast
	 * @param maxBlockCheckDepth the raycast will not check more than this many blocks for obstruction.
	 *                           if this distance between start_pos and the entity is larger,
	 *                           will check maxBlockCheckDepth/2 blocks after start_pos and
	 *                           maxBlockCheckDepth/2 blocks before the entity. there can be extreme
	 *                           performance issues if this is more than 500 depending on specs.
	 * @param throWater max number of blocks of water the raycast can go through without returning false
	 * @param throBlock max number of solid blocks the raycast can go through without returning false
	 * @return true if there is direct line of sight between pos and the eye position of entity
	 */
	public static boolean canEntitySeeEntity(Entity entity1, Entity entity2, int maxBlockCheckDepth, double throWater, double throBlock) {
		return canPosSeeEntity(entity1.getEyePosition(), entity2, maxBlockCheckDepth, throWater, throBlock);
	}

	private static boolean checkBlocksByRange(Level level, Vec3 pos, Vec3 look, int dist, double[] through) {
		int k = 0;
		while (k++ < dist) {
			pos = pos.add(look);

			// Convert Vec3 to BlockPos by rounding to nearest integer
			BlockPos bp = new BlockPos((int) pos.x(), (int) pos.y(), (int) pos.z());
			ChunkPos cp = new ChunkPos(bp);

			if (!level.hasChunk(cp.x, cp.z)) continue;
			BlockState block = level.getBlockState(bp);
			if (block == null || block.isAir()) continue;

			// Update block collision and liquid checks
			if (!block.isSuffocating(level, bp) && !block.getFluidState().isSource()) continue;

			if (through[0] <= 0 && through[1] <= 0) return false;
			if (block.getFluidState().getType().isSame(Fluids.WATER)) {
				if (through[0] > 0) {
					--through[0];
					continue;
				} else return false;
			}
			if (through[1] > 0) {
				--through[1];
				continue;
			} else return false;
		}
		return true;
	}



	/**
	 * @param level the level blocks are checked in
	 * @param start the start pos of the raycast
	 * @param end the end pos of the raycast
	 * @return the position of a block between start and end
	 */
	@Nullable
	public static Vec3 raycastBlock(Level level, Vec3 start, Vec3 end) {
		Vec3 diff = end.subtract(start);
		Vec3 dir = diff.normalize();
		double dist = diff.length();
		Vec3 pos = start;
		if (posBlocksMotion(level, pos)) return pos;
		int k = 1;
		while (k++ < dist) {
			pos = pos.add(dir);
			if (posBlocksMotion(level, pos)) return pos;
		}
		return null;
	}

	/**
	 * @param level the level the block is checked in
	 * @param pos the pos of the block being checked
	 * @return true if the chunk is loaded and there is a block at pos.
	 */
	public static boolean posBlocksMotion(Level level, Vec3 pos) {
		// Convert Vec3 to BlockPos
		BlockPos bp = new BlockPos((int) pos.x(), (int) pos.y(), (int) pos.z());
		ChunkPos cp = new ChunkPos(bp);

		if (!level.hasChunk(cp.x, cp.z)) return false;

		BlockState block = level.getBlockState(bp);
		if (block.isAir()) return false;

		return block.isSuffocating(level, bp);
	}


	/**
	 * @param entity distance from the entities feet
	 * @return entity's vertical distance from the ground. positive integer
	 */
	public static int getDistFromGround(Entity entity) {
		Level l = entity.level();
		int[] pos = {entity.getBlockX(), entity.getBlockY(), entity.getBlockZ()};
		int dist = 0;
		while (pos[1] >= -64) {
			BlockState block = l.getBlockState(new BlockPos(pos[0], pos[1], pos[2]));
			if (block != null && !block.isAir()) break;
			--pos[1];
			++dist;
		}
		return dist;
	}
	
	public static int getDistFromSeaLevel(Entity e) {
		return getDistFromSeaLevel(e.position().y, e.level());
	}
	
	public static int getDistFromSeaLevel(double yPos, Level level) {
		int sea = getSeaLevel(level);
		return (int)yPos - sea;
	}
	
	public static int getSeaLevel(Level level) {
		if (level.dimensionType().natural()) return 64;
		return 0;
	}

	/**
	 * @param entity start pos of raycast is entity eye position
	 * @param max the max distance of the raycast
	 * @return returns the position of a block the entity is looking at
	 */
	public static Vec3 getLookingAtBlockPos(Entity entity, int max) {
		Level level = entity.level();
		Vec3 look = entity.getLookAngle();
		Vec3 pos = entity.getEyePosition();

		for (int i = 0; i < max; ++i) {
			// Convert Vec3 to BlockPos
			BlockState block = level.getBlockState(new BlockPos((int) pos.x(), (int) pos.y(), (int) pos.z()));

			if (!block.isAir()) return pos;

			pos = pos.add(look);
		}

		return pos.add(look);
	}

	
	/**
	 * @param entity air pressure at entity position
	 * @return between 0 (no air pressure) and 1
	 */
	public static double getAirPressure(Entity entity) {
		DimensionType dt = entity.level().dimensionType();
		double space, surface;
		if (dt.natural()) {
			space = 2500;
			surface = 64;
		} else {
			space = 2000;
			surface = 0;
		}
		double scale = 1, exp = 2;
		double posY = entity.getY();
		if (posY <= surface) return scale;
		if (posY > space) return 0;
		posY -= surface;
		return Math.pow(Math.abs(posY-space), exp) * Math.pow(space, -exp);
	}
	
	public static boolean isHeadAboveWater(Entity entity) {
		return entity.isInWater() && !entity.isUnderWater();
	}

	/**
	 * turns the entity's head towards pos at headTurnRate degrees per tick.
	 * call this function every tick until the rotation is complete.
	 * @param entity
	 * @param pos
	 * @param headTurnRate
	 * @return true if the entity is looking at pos
	 */
	public static boolean entityLookAtPos(Entity entity, Vec3 pos, float headTurnRate) {
		Vec3 diff = pos.subtract(entity.getEyePosition());
		float yRot = UtilAngles.getYaw(diff);
		float xRot = UtilAngles.getPitch(diff);
		float newYRot = UtilAngles.rotLerp(entity.getYRot(), yRot, headTurnRate);
		float newXRot = UtilAngles.rotLerp(entity.getXRot(), xRot, headTurnRate);
		entity.setYRot(newYRot);
		entity.setXRot(newXRot);
		return xRot == newXRot && yRot == newYRot;
	}
	
	public static void mobLookAtPos(Mob mob, Vec3 pos, float headTurnRate) {
		mob.getLookControl().setLookAt(pos.x, pos.y, pos.z, headTurnRate, 360);
	}
	
	public static String getEntityIdName(Entity entity) {
		return EntityType.getKey(entity.getType()).getPath();
	}
	
	public static String getEntityModId(Entity entity) {
		return EntityType.getKey(entity.getType()).getNamespace();
	}
	
	public static int getRandomColor() {
		float hue = random.nextFloat();
		float saturation = (random.nextInt(4000) + 6000) / 10000f;
		float luminance = (random.nextInt(2000) + 8000) / 10000f;
		Color color = Color.getHSBColor(hue, saturation, luminance);
		return color.getRGB();
	}
	
	@Nullable
	public static EntityHitResult getEntityHitResultAtClip(Level level, Entity projectile, Vec3 start, Vec3 end, 
			AABB aabb, Predicate<Entity> filter, float inflateAmount) {
		double d0 = Double.MAX_VALUE;
		Entity entity = null;
		Vec3 pos = null;
		for(Entity entity1 : level.getEntities(projectile, aabb, filter)) {
			AABB aabb1 = entity1.getBoundingBox().inflate(inflateAmount);
			Optional<Vec3> optional = aabb1.clip(start, end);
			if (optional.isPresent()) {
				double d1 = start.distanceToSqr(optional.get());
				if (d1 < d0) {
					entity = entity1;
					d0 = d1;
					pos = optional.get();
				}
			}
		}
		return entity == null ? null : new EntityHitResult(entity, pos);
	}
	
	public static EntityType<?> getEntityType(String entityTypeKey, EntityType<?> alt) {
		if (entityTypeKey == null || entityTypeKey.isEmpty()) return alt;
		try {
			return ForgeRegistries.ENTITY_TYPES.getDelegate(
				new ResourceLocation(entityTypeKey)).get().get();
		} catch(NoSuchElementException e) { 
			return alt; 
		}
	}
	
	public static boolean doesEntityTypeExist(String entityTypeKey) {
		return ForgeRegistries.ENTITY_TYPES.containsKey(new ResourceLocation(entityTypeKey));
	}
	
	@Nullable
	public static Class<? extends Entity> getEntityClass(String className) {
		try {
			return Class.forName(className, false, UtilParse.class.getClassLoader()).asSubclass(Entity.class);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	public static boolean isPlayer(Entity entity) {
		if (entity == null) return false;
		return entity.getType().getDescriptionId().equals(EntityType.PLAYER.getDescriptionId());
	}
	
	public static void dropItemStack(Level level, ItemStack stack, Vec3 pos) {
		Containers.dropItemStack(level, pos.x, pos.y, pos.z, stack);
	}
	
	public static void dropItemStack(Entity entity, ItemStack stack) {
		dropItemStack(entity.level(), stack, entity.position());
	}
	
}
