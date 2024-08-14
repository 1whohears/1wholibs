package com.onewhohears.onewholibs.client.event.handler;

import com.onewhohears.onewholibs.OWLMod;
import com.onewhohears.onewholibs.client.event.RegisterCustomAnimsEvent;
import com.onewhohears.onewholibs.client.model.obj.customanims.EntityModelTransform;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OWLMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void registerCustomAnims(RegisterCustomAnimsEvent event) {
        event.addAnim("continuous_rotation", EntityModelTransform.ContinuousRotation::new);
    }

}
