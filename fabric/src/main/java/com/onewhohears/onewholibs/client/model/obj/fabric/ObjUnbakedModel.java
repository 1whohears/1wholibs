package com.onewhohears.onewholibs.client.model.obj.fabric;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.math.Vector3f;
import de.javagl.obj.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ObjUnbakedModel {

    private final ResourceLocation location;
    private final Obj obj;
    private final Map<String, Mtl> mtl;

    public ObjUnbakedModel(ResourceLocation location, Obj obj, Map<String, Mtl> mtl) {
        this.location = location;
        this.obj = obj;
        this.mtl = mtl;
    }

    public ResourceLocation getTexture() {
        String mapKd = "onewholibs:mtl_fail.png";
        if (obj.getNumMaterialGroups() > 0) {
            String mtlName = obj.getMaterialGroup(0).getName();
            if (mtl.containsKey(mtlName)) mapKd = mtl.get(mtlName).getMapKd();
        }
        if (!mapKd.endsWith(".png")) mapKd += ".png";
        String[] split = mapKd.split(":");
        String namespace, path;
        if (split.length == 1) {
            namespace = "minecraft";
            path = split[0];
        } else {
            namespace = split[0];
            path = split[1];
        }
        if (!path.startsWith("textures/")) path = "textures/" + path;
        return ResourceLocation.tryBuild(namespace, path);
    }

    public @NotNull ObjBakedModel bake() {
        //System.out.println("BAKING: "+location+" groups "+obj.getNumGroups());
        ResourceLocation texture = getTexture();
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(texture);
        ObjBakedModel.Builder builder = ObjBakedModel.builder();
        int numGroups = obj.getNumGroups();
        for (int i = 0; i < numGroups; ++i) {
            ObjGroup group = obj.getGroup(i);
            bakeGroup(builder, group.getName(), group, texture, sprite);
        }
        return builder.get();
    }

    private void bakeGroup(ObjBakedModel.Builder builder, String name, ObjGroup group,
                           ResourceLocation texture, TextureAtlasSprite sprite) {
        //System.out.println("baking group "+name+" texture "+texture+" faces "+group.getNumFaces());
        ObjBakedModel.PartBuilder<?> groupBuilder = builder.child(name);
        for (int i = 0; i < group.getNumFaces(); i++)
            bakeFace(groupBuilder, group.getFace(i), texture, sprite);
    }

    private void bakeFace(ObjBakedModel.PartBuilder<?> builder, ObjFace face,
                          ResourceLocation texture, TextureAtlasSprite sprite) {
        List<BakedQuad> quads = new ArrayList<>();

        List<ObjFace> tris = triangulateFace(face);
        for (ObjFace tri : tris) {
            Vector3f[] positions = new Vector3f[4];
            Vector3f[] normals   = new Vector3f[4];
            float[][] uvs        = new float[4][2];
            for (int i = 0; i < 3; i++) {
                int vIndex = tri.getVertexIndex(i);
                int tIndex = tri.getTexCoordIndex(i);
                int nIndex = tri.getNormalIndex(i);

                FloatTuple pos  = obj.getVertex(vIndex);
                FloatTuple uv   = obj.getTexCoord(tIndex);
                FloatTuple norm = (nIndex >= 0) ? obj.getNormal(nIndex) : null;

                positions[i] = vector3f(pos);
                uvs[i] = new float[] {uv.getX(), 1.0f - uv.getY()};
                normals[i] = (norm != null) ? vector3f(norm) : new Vector3f(0, 1, 0);
            }
            positions[3] = positions[2];
            uvs[3] = uvs[2];
            normals[3] = normals[2];

            int[] vertexData = packQuadData(positions, normals, uvs, sprite);
            Direction facing = Direction.getNearest(normals[0].x(), normals[0].y(), normals[0].z());

            quads.add(new BakedQuad(vertexData, -1, facing, sprite, true));
        }
        builder.addMesh(texture, quads);
    }

    private List<ObjFace> triangulateFace(ObjFace face) {
        int n = face.getNumVertices();
        if (n == 3) {
            return Collections.singletonList(face);
        }
        if (n == 4) {
            return fourVertsTo2Tris(face);
        }
        Obj temp = Objs.create();
        int[] vIdx = new int[n];
        int[] tIdx = new int[n];
        int[] nIdx = new int[n];
        for (int i = 0; i < n; i++) {
            vIdx[i] = face.getVertexIndex(i);
            tIdx[i] = face.getTexCoordIndex(i);
            nIdx[i] = face.getNormalIndex(i);
        }
        temp.addFace(vIdx, tIdx, nIdx);

        Obj tri = ObjUtils.triangulate(temp);
        List<ObjFace> faces = new ArrayList<>();
        for (int i = 0; i < tri.getNumFaces(); ++i) faces.add(tri.getFace(i));
        return faces;
    }

    private static @NotNull List<ObjFace> fourVertsTo2Tris(ObjFace face) {
        List<ObjFace> result = new ArrayList<>(2);
        result.add(new TriFace(face, 0, 1, 2));
        result.add(new TriFace(face, 0, 2, 3));
        return result;
    }

    private static class TriFace implements ObjFace {
        private final ObjFace parent;
        private final int[] indices;
        private TriFace(ObjFace parent, int... indices) {
            this.parent = parent;
            this.indices = indices;
        }
        @Override public int getNumVertices() { return 3; }
        @Override public boolean containsTexCoordIndices() { return parent.containsTexCoordIndices(); }
        @Override public boolean containsNormalIndices() { return parent.containsNormalIndices(); }
        @Override public int getVertexIndex(int i) { return parent.getVertexIndex(indices[i]); }
        @Override public int getTexCoordIndex(int i) { return parent.getTexCoordIndex(indices[i]); }
        @Override public int getNormalIndex(int i) { return parent.getNormalIndex(indices[i]); }
    }

    public static Vector3f vector3f(FloatTuple tuple) {
        return new Vector3f(tuple.getX(), tuple.getY(), tuple.getZ());
    }

    private int[] packQuadData(Vector3f[] positions, Vector3f[] normals, float[][] uvs, TextureAtlasSprite sprite) {
        int[] data = new int[DefaultVertexFormat.BLOCK.getVertexSize() / 4 * 4];
        for (int i = 0; i < 4; i++) {
            int offset = i * (DefaultVertexFormat.BLOCK.getVertexSize() / 4);

            data[offset]     = Float.floatToRawIntBits(positions[i].x());
            data[offset + 1] = Float.floatToRawIntBits(positions[i].y());
            data[offset + 2] = Float.floatToRawIntBits(positions[i].z());

            data[offset + 3] = -1;

            data[offset + 4] = Float.floatToRawIntBits(uvs[i][0]);
            data[offset + 5] = Float.floatToRawIntBits(uvs[i][1]);

            data[offset + 6] = 0;
            data[offset + 7] = 0;
        }
        return data;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public Obj getObj() {
        return obj;
    }

    public Map<String, Mtl> getMtl() {
        return mtl;
    }
}
