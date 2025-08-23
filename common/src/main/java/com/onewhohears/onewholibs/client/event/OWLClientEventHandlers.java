package com.onewhohears.onewholibs.client.event;

import com.onewhohears.onewholibs.client.model.obj.customanims.CustomAnims;
import com.onewhohears.onewholibs.client.model.obj.customanims.EntityModelTransform;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.BasicControllers;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.ControllableAnimPlayer;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KFAnimPlayers;
import com.onewhohears.onewholibs.init.OWLModItems;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import net.minecraft.client.Minecraft;

public class OWLClientEventHandlers {

    public static void init() {
        ClientLifecycleEvent.CLIENT_SETUP.register(OWLClientEventHandlers::onClientSetup);
    }

    public static void onClientSetup(Minecraft minecraft) {
        // REGISTER BUILT IN CLIENT ANIMATION STUFF
        CustomAnims.addAnim("continuous_rotation", EntityModelTransform.ContinuousRotation::new);
        CustomAnims.addAnim("always_hide", EntityModelTransform.AlwaysHide::new);
        KFAnimPlayers.addAnimationPlayerFactory("always", (data) -> new ControllableAnimPlayer<>(data,
                entity -> true, BasicControllers.continuous()));
        KFAnimPlayers.addAnimationPlayerFactory("ground_move", (data) -> new ControllableAnimPlayer<>(data,
                entity -> entity.isOnGround() && entity.getDeltaMovement().lengthSqr() > 0.0001,
                BasicControllers.continuous()));
        KFAnimPlayers.addAnimationPlayerFactory("air_move", (data) -> new ControllableAnimPlayer<>(data,
                entity -> !entity.isOnGround() && entity.getDeltaMovement().lengthSqr() > 0.0001,
                BasicControllers.continuous()));
    }

}
