package com.onewhohears.onewholibs.client.event;

import com.google.common.collect.ImmutableBiMap;
import com.onewhohears.onewholibs.client.model.obj.customanims.CustomAnims;
import net.minecraftforge.eventbus.api.Event;

import java.util.HashMap;
import java.util.Map;

public class RegisterCustomAnimsEvent extends Event {

    private final Map<String, CustomAnims.AnimationFactory> animFacs = new HashMap<>();

    public RegisterCustomAnimsEvent() {
    }

    public void addAnim(String id, CustomAnims.AnimationFactory factory) {
        animFacs.put(id, factory);
    }

    public Map<String, CustomAnims.AnimationFactory> getAnims() {
        return ImmutableBiMap.copyOf(animFacs);
    }

}
