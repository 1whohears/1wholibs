package com.onewhohears.onewholibs;

import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.client.model.obj.customanims.CustomAnims;
import com.onewhohears.onewholibs.client.model.obj.customanims.EntityModelTransform;
import com.onewhohears.onewholibs.common.network.PacketHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(OWLMod.MODID)
public class OWLMod {

    public static final String MODID = "onewholibs";
    private static final Logger LOGGER = LogUtils.getLogger();

    public OWLMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        PacketHandler.register();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        CustomAnims.addAnim("continuous_rotation", EntityModelTransform.ContinuousRotation::new);
    }

}
