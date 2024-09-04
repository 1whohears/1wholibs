package com.onewhohears.onewholibs.mixin;

import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.client.model.obj.ObjModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(ObjModel.class)
public interface ObjModelAccess {
    @Accessor(value = "positions", remap = false)
    List<Vector3f> getPositions();
    @Accessor(value = "texCoords", remap = false)
    List<Vec2> getTexCoords();
    @Accessor(value = "normals", remap = false)
    List<Vector3f> getNormals();
    @Accessor(value = "colors", remap = false)
    List<Vector4f> getColors();
    @Accessor(value = "parts", remap = false)
    Map<String, ObjModel.ModelGroup> getParts();
}
