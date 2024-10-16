package com.onewhohears.onewholibs;

import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.client.model.obj.customanims.CustomAnims;
import com.onewhohears.onewholibs.client.model.obj.customanims.EntityModelTransform;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.BasicControllers;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.ControllableAnimPlayer;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KFAnimPlayers;
import com.onewhohears.onewholibs.common.network.PacketHandler;
import com.onewhohears.onewholibs.init.ModEntities;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * @author 1whohears
 */
@Mod(OWLMod.MODID)
public class OWLMod {

    public static final String MODID = "onewholibs";
    private static final Logger LOGGER = LogUtils.getLogger();

    public OWLMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEntities.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        PacketHandler.register();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        CustomAnims.addAnim("continuous_rotation", EntityModelTransform.ContinuousRotation::new);
        CustomAnims.addAnim("always_hide", EntityModelTransform.AlwaysHide::new);
        KFAnimPlayers.addAnimationPlayerFactory("always", (data) -> new ControllableAnimPlayer<>(data,
                entity -> true, BasicControllers.continuous()));
        KFAnimPlayers.addAnimationPlayerFactory("ground_move", (data) -> new ControllableAnimPlayer<>(data,
                entity -> entity.onGround() && entity.getDeltaMovement().lengthSqr() > 0.0001,
                BasicControllers.continuous()));
        KFAnimPlayers.addAnimationPlayerFactory("air_move", (data) -> new ControllableAnimPlayer<>(data,
                entity -> !entity.onGround() && entity.getDeltaMovement().lengthSqr() > 0.0001,
                BasicControllers.continuous()));
    }

}
