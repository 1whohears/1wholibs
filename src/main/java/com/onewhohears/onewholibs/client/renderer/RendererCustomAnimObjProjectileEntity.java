package com.onewhohears.onewholibs.client.renderer;

import com.onewhohears.onewholibs.client.model.obj.ObjEntityModel;
import com.onewhohears.onewholibs.entity.CustomAnimProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class RendererCustomAnimObjProjectileEntity<T extends CustomAnimProjectile<?,?>> extends RendererObjEntity<T> {

    public RendererCustomAnimObjProjectileEntity(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected ObjEntityModel<T> getModel(T entity) {
        return entity.getAssets().getModel();
    }
}
