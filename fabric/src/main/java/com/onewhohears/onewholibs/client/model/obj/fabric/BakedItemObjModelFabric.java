package com.onewhohears.onewholibs.client.model.obj.fabric;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BakedItemObjModelFabric implements BakedModel {

    private final List<BakedQuad> quads;
    private final TextureAtlasSprite particle;
    private final ItemTransforms transforms;
    private final ItemOverrides overrides;

    public BakedItemObjModelFabric(ObjUnbakedModel unbakedObjModel, ObjBakedModel bakedObjModel) {
        this.quads = bakedObjModel.getItemQuads();
        this.particle = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(unbakedObjModel.getTexture());
        this.transforms = ItemTransforms.NO_TRANSFORMS; // FIXME calc transforms for this obj model item
        this.overrides = ItemOverrides.EMPTY;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction,
                                             RandomSource randomSource) {
        return quads;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return particle;
    }

    @Override
    public @NotNull ItemTransforms getTransforms() {
        return transforms;
    }

    @Override
    public @NotNull ItemOverrides getOverrides() {
        return overrides;
    }
}
