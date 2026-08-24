package com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.state_machine;

import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.ControllableAnimPlayer;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KFAnimData;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KeyframeAnimationController;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KeyframeAnimationTrigger;
import com.onewhohears.onewholibs.entity.StateMachineAnimEntity;
import net.minecraft.world.entity.Entity;

public class StateMachineAnimPlayer<T extends Entity & StateMachineAnimEntity> extends ControllableAnimPlayer<T> {

    public StateMachineAnimPlayer(KFAnimData stats, KeyframeAnimationTrigger<T> trigger,
                                  KeyframeAnimationController<T> controller) {
        super(stats, trigger, controller);
    }

    public StateMachineAnimPlayer(KFAnimData stats, KeyframeAnimationController<T> controller) {
        this(stats, entity -> entity.isAnimDataIdActive(stats.getId()), controller);
    }

    public StateMachineAnimPlayer(KFAnimData stats) {
        this(stats, (entity, partialTicks, animationLength) -> entity.getAnimSeconds(stats.getId(), partialTicks, animationLength));
    }

}
