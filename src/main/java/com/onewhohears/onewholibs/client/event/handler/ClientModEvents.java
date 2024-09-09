package com.onewhohears.onewholibs.client.event.handler;

import com.onewhohears.onewholibs.OWLMod;
import com.onewhohears.onewholibs.client.model.obj.ObjEntityModels;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KFAnimPlayers;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.bbanims.BlockBenchAnims;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author 1whohears
 */
@Mod.EventBusSubscriber(modid = OWLMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerClientReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ObjEntityModels.get());
        event.registerReloadListener(BlockBenchAnims.get());
        event.registerReloadListener(KFAnimPlayers.get());
    }

}
