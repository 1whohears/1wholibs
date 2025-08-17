package com.onewhohears.onewholibs.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.onewhohears.onewholibs.client.model.obj.ObjModelHandler;
import com.onewhohears.onewholibs.item.ObjModelItem;
import com.onewhohears.onewholibs.client.model.obj.ObjEntityModels;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class RendererObjModelItems extends BlockEntityWithoutLevelRenderer {

    private static RendererObjModelItems instance;

    public static RendererObjModelItems get() {
        return instance;
    }

    public static void register() {
        instance = createNew();
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, instance);
    }

    public static RendererObjModelItems createNew() {
        Minecraft m = Minecraft.getInstance();
        return new RendererObjModelItems(m.getBlockEntityRenderDispatcher(), m.getEntityModels());
    }

    private final Map<String, ItemObjModelData> models = new HashMap<>();

    protected RendererObjModelItems(BlockEntityRenderDispatcher blockEntityRenderDispatcher,
                                    EntityModelSet entityModelSet) {
        super(blockEntityRenderDispatcher, entityModelSet);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        models.clear();
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Item item = stack.getItem();
        if (item instanceof ObjModelItem objItem) {
            String preset = objItem.getPreset(stack);
            ItemObjModelData model = models.get(preset);
            if (model == null) {
                String modelId = objItem.getObjModelId(preset);
                // the model id needs to be set in client stats json thing
                ObjModelHandler handler = ObjEntityModels.get().getObjModelHandler(modelId);
                ObjEntityModels.ModelOverrides override = ObjEntityModels.get().getModelOverride(modelId);
                ObjEntityModels.ModelOverrides itemModelOverrides = objItem.getItemModelOverrides(preset);
                model = new ItemObjModelData(handler, override, itemModelOverrides);
                models.put(preset, model);
            }
            model.render(transformType, poseStack, buffer, packedLight, packedOverlay);
        } 
    }

    public static final Map<String, Matrix4f> EMPTY_TRANSFORMS = ImmutableMap.of();

    public static class ItemObjModelData {
        public static final float SIZE_SCALE_FACTOR = 1.25f;
        public final ObjModelHandler handler;
        public final ObjEntityModels.ModelOverrides modelOverrides, itemModelOverrides;
        public final Vec3 center;
        public final float scale;
        public ItemObjModelData(
                ObjModelHandler handler,
                ObjEntityModels.ModelOverrides modelOverrides,
                ObjEntityModels.ModelOverrides itemModelOverrides
        ) {
            this.handler = handler;
            this.modelOverrides = modelOverrides;
            this.itemModelOverrides = itemModelOverrides;
            Vec3 size = handler.getSize();
            float maxSize = (float)Math.max(size.z(), Math.max(size.x(), size.y()));
            center = handler.getCenter();
            scale = SIZE_SCALE_FACTOR / maxSize;
        }
        public void render(ItemTransforms.TransformType transformType, PoseStack poseStack,
                           MultiBufferSource buffer, int packedLight, int packedOverlay) {
            poseStack.pushPose();
            modelOverrides.applyRotation(poseStack);
            itemModelOverrides.apply(poseStack);
            if (transformType == ItemTransforms.TransformType.GUI) {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(30));
                poseStack.mulPose(Vector3f.YP.rotationDegrees(45));
            } else if (transformType == ItemTransforms.TransformType.FIXED) {
                poseStack.translate(0.5, 0.5, 0.35);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(-90));
            } else if (transformType == ItemTransforms.TransformType.GROUND) {
                poseStack.translate(0.5, 0.5, 0.5);
            } else if (transformType == ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {
                poseStack.translate(0.5, 0.5, 0.3);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(30));
                poseStack.mulPose(Vector3f.YP.rotationDegrees(225));
            } else if (transformType == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
                poseStack.translate(0.5, 0.5, 0.3);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(30));
                poseStack.mulPose(Vector3f.YP.rotationDegrees(135));
            } else if (transformType == ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND) {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(90));
                poseStack.mulPose(Vector3f.YP.rotationDegrees(225));
            } else if (transformType == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND) {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(90));
                poseStack.mulPose(Vector3f.YP.rotationDegrees(135));
            } else if (transformType == ItemTransforms.TransformType.HEAD) {
                poseStack.translate(0.5, 1.2, 0.5);
                poseStack.scale(2f, 2f, 2f);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(180));
            } else if (transformType == ItemTransforms.TransformType.NONE) {
                poseStack.translate(0.5, 0.5, 0.5);
            }
            poseStack.scale(scale, scale, scale);
            poseStack.translate((float) -center.x(), (float) -center.y(), (float) -center.z());
            handler.render(poseStack, buffer, 0, packedLight, packedOverlay,
                    EMPTY_TRANSFORMS, RenderType::entitySolid);
            poseStack.popPose();
        }
    }
}
