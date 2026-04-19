package com.onewhohears.onewholibs;

import com.onewhohears.onewholibs.client.event.OWLClientEventHandlers;
import com.onewhohears.onewholibs.client.model.obj.ObjEntityModels;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.KFAnimPlayers;
import com.onewhohears.onewholibs.client.model.obj.customanims.keyframe.bbanims.BlockBenchAnims;
import com.onewhohears.onewholibs.client.renderer.RendererObjModelItems;
import com.onewhohears.onewholibs.common.command.CustomGameRules;
import com.onewhohears.onewholibs.common.event.OWLCommonEventHandlers;
import com.onewhohears.onewholibs.common.event.OWLReloadListener;
import com.onewhohears.onewholibs.common.event.ServerHolder;
import com.onewhohears.onewholibs.common.network.OWLPacketHandler;
import com.onewhohears.onewholibs.init.OWLModEntities;
import com.onewhohears.onewholibs.init.OWLModItems;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;

/**
 * @author <a href="https://github.com/1whohears">1whohears</a>
 * @author <a href="https://github.com/kawaiicakes">kawaiicakes</a>
 */
public class OWLMod {
    public static final String MOD_ID = "onewholibs";

    public static void init() {
        OWLPacketHandler.init();
        if (Platform.getEnvironment() == Env.SERVER) {
            ServerHolder.init();
        }
        CustomGameRules.register();
        OWLModItems.init();
        OWLModEntities.init();
        OWLCommonEventHandlers.init();
        OWLReloadListener.register();
    }

    public static void clientInit() {
        ObjEntityModels.register();
        BlockBenchAnims.register();
        KFAnimPlayers.register();
        RendererObjModelItems.register();
        OWLClientEventHandlers.init();
    }
}
