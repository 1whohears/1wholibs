package com.onewhohears.onewholibs.forge;

import com.onewhohears.onewholibs.OWLMod;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KeyframeAnimsEntityModel;
import com.onewhohears.onewholibs.client.renderer.RendererObjEntity;
import com.onewhohears.onewholibs.common.network.PacketHandler;
import com.onewhohears.onewholibs.init.OWLModEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import dev.architectury.platform.forge.EventBuses;

@Mod(OWLMod.MOD_ID)
public final class OWLModForge {
    public static final DeferredRegister<EntityType<?>> ENTITY_REGISTER_FORGE
            = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, OWLMod.MOD_ID);

    public OWLModForge() {
        // Leave this here for now otherwise we will be incompatible w/ older versions of Forge
        @SuppressWarnings("removal")
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(OWLMod.MOD_ID, modEventBus);

        ENTITY_REGISTER_FORGE.register("test", OWLModEntities.testEntity());

        modEventBus.addListener(this::commonSetup);

        ENTITY_REGISTER_FORGE.register(modEventBus);

        OWLMod.init();
    }

    // Compatible with newer versions of Forge
    public OWLModForge(FMLJavaModLoadingContext loadingContext) {
        IEventBus modEventBus = loadingContext.getModEventBus();
        EventBuses.registerModEventBus(OWLMod.MOD_ID, modEventBus);

        ENTITY_REGISTER_FORGE.register("test", OWLModEntities.testEntity());

        modEventBus.addListener(this::commonSetup);

        ENTITY_REGISTER_FORGE.register(modEventBus);

        OWLMod.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        PacketHandler.register();
    }

    @Mod.EventBusSubscriber(modid = OWLMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            //noinspection removal
            event.registerEntityRenderer(
                    RegistryObject.create(
                            new ResourceLocation(OWLMod.MOD_ID, "test"),
                            ForgeRegistries.ENTITY_TYPES
                    ).get(),
                    context -> new RendererObjEntity<>(context,
                            new KeyframeAnimsEntityModel<>("ciws_test", "turret_test_anim"))
            );
        }
    }
}
