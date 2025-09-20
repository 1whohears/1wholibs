package com.onewhohears.onewholibs.client.model.obj;

import com.mojang.blaze3d.vertex.PoseStack;
import com.onewhohears.onewholibs.util.math.Mat4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ObjBakedModel {

    public static Builder builder()
    {
        return new Builder();
    }

    private final List<Component> components = new ArrayList<>();

    protected ObjBakedModel() {}

    public void render(PoseStack poseStack, MultiBufferSource bufferSource,
                       Function<ResourceLocation, RenderType> renderType,
                       int lightmap, int overlay, float partialTicks,
                       Map<String, Mat4f> transforms) {
        for (var component : components)
            component.render(poseStack, bufferSource, renderType, lightmap, overlay, transforms);
    }

    public Component getCreateComponent(String name) {
        for (var component : components)
            if (component.name.equals(name))
                return component;
        Component c = new Component(name);
        components.add(c);
        return c;
    }

    public List<Component> getComponents() {
        return components;
    }

    public static class Component {
        private final String name;
        private final List<Component> children = new ArrayList<>();
        private final List<Mesh> meshes = new ArrayList<>();

        public Component(String name) {
            this.name = name;
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferSource,
                           Function<ResourceLocation, RenderType> renderType,
                           int lightmap, int overlay, Map<String, Mat4f> context) {
            Mat4f matrix = context.get(name);
            if (matrix != null) {
                poseStack.pushPose();
                poseStack.mulPoseMatrix(matrix.convert());
            }

            for (var part : children)
                part.render(poseStack, bufferSource, renderType, lightmap, overlay, context);

            for (var mesh : meshes)
                mesh.render(poseStack, bufferSource, renderType, lightmap, overlay);

            if (matrix != null)
                poseStack.popPose();
        }

        public Component getCreateChild(String name) {
            for (var component : children)
                if (component.name.equals(name))
                    return component;
            Component c = new Component(name);
            children.add(c);
            return c;
        }

        public String getName() {
            return name;
        }

        public List<Component> getChildren() {
            return children;
        }

        public List<Mesh> getMeshes() {
            return meshes;
        }
    }

    public static class Mesh {
        private static final float[] ONES = new float[] { 1.0F, 1.0F, 1.0F, 1.0F };

        private final ResourceLocation texture;
        private final List<BakedQuad> quads = new ArrayList<>();

        public Mesh(ResourceLocation texture)
        {
            this.texture = texture;
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferSource,
                           Function<ResourceLocation, RenderType> renderType,
                           int lightmap, int overlay) {
            var consumer = bufferSource.getBuffer(renderType.apply(texture));
            for (var quad : quads) {
                consumer.putBulkData(poseStack.last(), quad, ONES, 1, 1, 1,
                        new int[] { lightmap, lightmap, lightmap, lightmap }, overlay, true);
            }
        }

        public ResourceLocation getTexture() {
            return texture;
        }

        public List<BakedQuad> getQuads() {
            return quads;
        }
    }

    public static class Builder {
        private final ObjBakedModel renderable = new ObjBakedModel();

        private Builder() {}

        public PartBuilder<?> child(String name) {
            String[] split = name.split("/");
            if (split.length == 1) {
                var child = new Component(name);
                renderable.components.add(child);
                return new PartBuilder<>(this, child);
            }
            Component parent = renderable.getCreateComponent(split[0]);
            PartBuilder<?> parentBuilder = new PartBuilder<>(this, parent);
            PartBuilder<?> childBuilder = null;
            for (int i = 1; i < split.length; ++i) {
                childBuilder = parentBuilder.child(split[i]);
                parentBuilder = childBuilder;
            }
            return (childBuilder != null) ? childBuilder : parentBuilder;
        }

        public ObjBakedModel get() {
            return renderable;
        }
    }

    public static class PartBuilder<T> {
        private final T parent;
        private final Component component;

        private PartBuilder(T parent, Component component) {
            this.parent = parent;
            this.component = component;
        }

        public PartBuilder<PartBuilder<T>> child(String name) {
            var child = component.getCreateChild(name);
            return new PartBuilder<>(this, child);
        }

        public PartBuilder<T> addMesh(ResourceLocation texture, List<BakedQuad> quads) {
            var mesh = new Mesh(texture);
            mesh.quads.addAll(quads);
            component.meshes.add(mesh);
            return this;
        }

        public T end() {
            return parent;
        }
    }

}
