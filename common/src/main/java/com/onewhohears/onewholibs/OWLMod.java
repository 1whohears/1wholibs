package com.onewhohears.onewholibs;

import com.mojang.logging.LogUtils;
import com.onewhohears.onewholibs.common.event.OWLArchEventHandlers;
import com.onewhohears.onewholibs.common.event.OWLEvents;
import com.onewhohears.onewholibs.common.event.OWLReloadListener;
import com.onewhohears.onewholibs.common.event.ServerHolder;
import com.onewhohears.onewholibs.data.jsonpreset.JsonPresetReloadListener;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;

import java.util.List;

/**
 * @author <a href="https://github.com/1whohears">1whohears</a>
 * @author <a href="https://github.com/kawaiicakes">kawaiicakes</a>
 */
public class OWLMod {
    public static final String MOD_ID = "onewholibs";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        ServerHolder.init();
        OWLArchEventHandlers.init();
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
        com.onewhohears.onewholibs.client.renderer.RendererCustomAnimObjEntity
        com.onewhohears.onewholibs.client.renderer.RendererCustomAnimObjProjectile
        com.onewhohears.onewholibs.client.renderer.RendererObjModelItems
        com.onewhohears.onewholibs.client.model.obj.ObjEntityModel
        com.onewhohears.onewholibs.client.model.obj.ObjEntityModels
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
