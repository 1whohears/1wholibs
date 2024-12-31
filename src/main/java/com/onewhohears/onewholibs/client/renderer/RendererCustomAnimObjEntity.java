package com.onewhohears.onewholibs.client.renderer;

import com.onewhohears.onewholibs.client.model.obj.ObjEntityModel;
import com.onewhohears.onewholibs.entity.CustomAnimEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class RendererCustomAnimObjEntity<T extends CustomAnimEntity<?,?>> extends RendererObjEntity<T> {

    protected RendererCustomAnimObjEntity(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected ObjEntityModel<T> getModel(T entity) {
        return entity.getAssets().getModel();
    }
}
