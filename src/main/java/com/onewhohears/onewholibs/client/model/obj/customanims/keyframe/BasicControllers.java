package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe;

import net.minecraft.world.entity.Entity;

public class BasicControllers {

    public static <T extends Entity> KeyframeAnimationController<T> continuous() {
        return (entity, partialTicks, animationLength) -> (((float)entity.tickCount + partialTicks) * 0.05f) % animationLength;
    }

}
