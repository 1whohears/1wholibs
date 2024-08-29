package com.onewhohears.onewholibs.data.jsonpreset;

import net.minecraft.nbt.CompoundTag;

/**
 * {@link JsonPresetStats} are meant to be immutable. Thus, {@link JsonPresetInstance} are
 * their dynamic counterparts. Save and load NBT data with {@link #writeNBT()} and
 * {@link #readNBT(CompoundTag)}. See {@link JsonPresetReloadListener#createInstanceFromNbt(CompoundTag)}.
 * @author 1whohears
 */
public abstract class JsonPresetInstance<T extends JsonPresetStats> {
	
	private final PresetStatsHolder<T> stats;
	
	protected JsonPresetInstance(PresetStatsHolder<T> stats) {
		this.stats = stats;
	}
	
	public T getStats() {
		return stats.get();
	}
	
	public String getStatsId() {
		return getStats().getId();
	}
	
	public String getTypeId() {
		return getStats().getType().getId();
	}
	
	public void readNBT(CompoundTag tag) {
		
	}
	
	public CompoundTag writeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putString("presetId", getStatsId());
		return tag;
	}
	
	@Override
	public String toString() {
		return "("+getTypeId()+":"+getStatsId()+")";
	}
	
}
