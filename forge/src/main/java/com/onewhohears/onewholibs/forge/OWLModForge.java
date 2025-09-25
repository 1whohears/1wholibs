package com.onewhohears.onewholibs.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.onewhohears.onewholibs.OWLMod;
import com.onewhohears.onewholibs.client.model.obj.ObjEntityModels;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KeyframeAnimsEntityModel;
import com.onewhohears.onewholibs.client.renderer.RendererObjEntity;
import com.onewhohears.onewholibs.data.jsonpreset.test.TestPresetGenerator;
import com.onewhohears.onewholibs.util.math.Vec3f;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import dev.architectury.platform.forge.EventBuses;

@Mod(OWLMod.MOD_ID)
public final class OWLModForge {

    // Leave this here for now otherwise we will be incompatible w/ older versions of Forge
    public OWLModForge() {
        @SuppressWarnings("removal")
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(OWLMod.MOD_ID, modEventBus);

        modEventBus.addListener(this::onGatherData);

        OWLMod.init();
        if (Platform.getEnvironment() == Env.CLIENT && !DatagenModLoader.isRunningDataGen()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> OWLMod::clientInit);
        }
    }

    // Compatible with newer versions of Forge
    public OWLModForge(FMLJavaModLoadingContext loadingContext) {
        IEventBus modEventBus = loadingContext.getModEventBus();
        EventBuses.registerModEventBus(OWLMod.MOD_ID, modEventBus);

        modEventBus.addListener(this::onGatherData);

        OWLMod.init();
        if (Platform.getEnvironment() == Env.CLIENT && !DatagenModLoader.isRunningDataGen()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> OWLMod::clientInit);
        }
    }

    private void onGatherData(GatherDataEvent event) {
        if (event.includeServer()) {
            TestPresetGenerator.register(event.getGenerator());
        }
    }

    @Mod.EventBusSubscriber(modid = OWLMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            //noinspection removal
            event.registerEntityRenderer(
                    RegistryObject.create(
                            ResourceLocation.tryBuild(OWLMod.MOD_ID, "test"),
                            ForgeRegistries.ENTITY_TYPES
                    ).get(),
                    context -> new RendererObjEntity<>(context,
                            //new KeyframeAnimsEntityModel<>("ciws_test", "turret_test_anim"))
                            new KeyframeAnimsEntityModel<Entity>("alexis_plane") {
                                @Override
                                protected void rotate(Entity entity, float partialTicks, PoseStack poseStack) {
                                    super.rotate(entity, partialTicks, poseStack);
                                    poseStack.mulPose(Vec3f.ZP.rotationDegrees(entity.tickCount).convert());
                                }
                            })
            );
        }
        @SubscribeEvent
        public static void onBakingComplete(ModelEvent.BakingCompleted event) {
            /*
                fabric allows TextureAtlasSprite to be created whenever,
                but forge is cringe and throws getAtlasTexture called too early! errors.
                so this is required.
            */
            ObjEntityModels.get().bakeModels();
        }
    }
}
