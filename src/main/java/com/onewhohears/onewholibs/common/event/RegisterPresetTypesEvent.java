package com.onewhohears.onewholibs.common.event;

import net.minecraftforge.eventbus.api.Event;

/**
 * Fired on client and server side by {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 * Use {@link com.onewhohears.onewholibs.data.jsonpreset.JsonPresetReloadListener#addPresetType(com.onewhohears.onewholibs.data.jsonpreset.JsonPresetType)}
 * to add custom preset types.
 * @author 1whohears
 */
public class RegisterPresetTypesEvent extends Event {

	@Override
	public boolean isCancelable() {
		return false;
	}
	
}
