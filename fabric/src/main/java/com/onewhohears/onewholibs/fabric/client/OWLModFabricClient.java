package com.onewhohears.onewholibs.fabric.client;

import com.onewhohears.onewholibs.OWLMod;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KeyframeAnimsEntityModel;
import com.onewhohears.onewholibs.client.renderer.RendererObjEntity;
import dev.felnull.specialmodelloader.api.event.SpecialModelLoaderEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public final class OWLModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(
                Registry.ENTITY_TYPE.get(new ResourceLocation(OWLMod.MOD_ID, "test")),
                context -> new RendererObjEntity<>(context,
                        new KeyframeAnimsEntityModel<>("ciws_test", "turret_test_anim")
                )
        );
        SpecialModelLoaderEvents.LOAD_SCOPE.register(() ->
                (resourceManager, location) ->
                        OWLMod.MOD_ID.equals(location.getNamespace()));
    }
}
