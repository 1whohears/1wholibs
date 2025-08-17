package com.onewhohears.onewholibs;

import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.client.event.OWLClientEventHandlers;
import com.onewhohears.onewholibs.client.model.obj.ObjEntityModels;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KFAnimPlayers;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.bbanims.BlockBenchAnims;
import com.onewhohears.onewholibs.client.renderer.RendererObjModelItems;
import com.onewhohears.onewholibs.common.event.OWLCommonEventHandlers;
import com.onewhohears.onewholibs.common.event.OWLEvents;
import com.onewhohears.onewholibs.common.event.OWLReloadListener;
import com.onewhohears.onewholibs.common.event.ServerHolder;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetReloadListener;
import dev.architectury.platform.Platform;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.registry.registries.Registries;
import dev.architectury.utils.Env;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author <a href="https://github.com/1whohears">1whohears</a>
 * @author <a href="https://github.com/kawaiicakes">kawaiicakes</a>
 */
public class OWLMod {
    public static final String MOD_ID = "onewholibs";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Supplier<Registries> REGISTRIES = Suppliers.memoize(() -> Registries.get(MOD_ID));

    public static void init() {
        if (Platform.getEnvironment() == Env.CLIENT) {
            ObjEntityModels.register();
            BlockBenchAnims.register();
            KFAnimPlayers.register();
            RendererObjModelItems.register();
            OWLClientEventHandlers.init();
        } else {
            ServerHolder.init();
        }
        OWLCommonEventHandlers.init();
        OWLReloadListener.register();
        OWLEvents.registerPresetTypesEvent();
        List<JsonPresetReloadListener<?>> listeners = OWLEvents.getJsonPresetReloadListeners();
        listeners.forEach(listener -> {
            listener.registerDefaultPresetTypes();
            ReloadListenerRegistry.register(PackType.SERVER_DATA, listener);
        });
    }

    // TODO - Agnosticize tabulated classes and their related stuff:
    /*
        com.onewhohears.onewholibs.client.renderer.RendererObjModelItems
        com.onewhohears.onewholibs.client.model.obj.ObjModelParser
        com.onewhohears.onewholibs.item.ObjModelItem
        com.onewhohears.onewholibs.mixin.CompositeRenderableBuilderMixin
        com.onewhohears.onewholibs.mixin.ModelGroupAccess
        com.onewhohears.onewholibs.mixin.ModelObjectAccess
        com.onewhohears.onewholibs.mixin.ObjLoaderMixin
        com.onewhohears.onewholibs.mixin.ObjModelAccess
        com.onewhohears.onewholibs.util.UtilClientReflection
     */
}
