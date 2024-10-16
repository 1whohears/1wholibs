package com.onewhohears.onewholibs.client.model.obj;

import com.onewhohears.onewholibs.mixin.ModelGroupAccess;
import com.onewhohears.onewholibs.mixin.ModelObjectAccess;
import com.onewhohears.onewholibs.mixin.ObjModelAccess;
import com.onewhohears.onewholibs.util.UtilClientReflection;
import com.onewhohears.onewholibs.util.UtilParse;
import joptsimple.internal.Strings;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjMaterialLibrary;
import net.minecraftforge.client.model.obj.ObjModel;
import net.minecraftforge.client.model.obj.ObjTokenizer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class ObjModelParser {
    /**
     * forge's obj model parse code does not correctly deal with multi layered groups.
     * so I copied forge's obj model parse code, made some accessor mixins/reflection code,
     * and modified how it handles objects (o) and groups (g).
     */
    public static ObjModel parse(ObjTokenizer tokenizer, ObjModel.ModelSettings settings) throws IOException {
        Map<String, String> deprecationWarnings = Map.of();
        ResourceLocation modelLocation = settings.modelLocation();
        String materialLibraryOverrideLocation = settings.mtlOverride();
        ObjModel model = UtilClientReflection.createNewObjModel(settings);
        String modelDomain = modelLocation.getNamespace();
        String modelPath = modelLocation.getPath();
        int lastSlash = modelPath.lastIndexOf(47);
        if (lastSlash >= 0) {
            modelPath = modelPath.substring(0, lastSlash + 1);
        } else {
            modelPath = "";
        }
        ObjMaterialLibrary mtllib = ObjMaterialLibrary.EMPTY;
        ObjMaterialLibrary.Material currentMat = null;
        String currentSmoothingGroup = null;
        ObjModel.ModelGroup currentGroup = null;
        ObjModel.ModelObject currentObject = null;
        Object currentMesh = null;
        if (materialLibraryOverrideLocation != null) {
            if (materialLibraryOverrideLocation.contains(":")) {
                mtllib = ObjLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(materialLibraryOverrideLocation));
            } else {
                mtllib = ObjLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(modelDomain, modelPath + materialLibraryOverrideLocation));
            }
        }
        String[] line;
        while ((line = tokenizer.readAndSplitLine(true)) != null) {
            String lib;
            switch (line[0]) {
                case "mtllib":
                    if (materialLibraryOverrideLocation == null) {
                        lib = line[1];
                        if (lib.contains(":")) {
                            mtllib = ObjLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(lib));
                        } else {
                            mtllib = ObjLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(modelDomain, modelPath + lib));
                        }
                    }
                    break;
                case "usemtl":
                    lib = Strings.join(Arrays.copyOfRange(line, 1, line.length), " ");
                    ObjMaterialLibrary.Material newMat = mtllib.getMaterial(lib);
                    if (Objects.equals(newMat, currentMat)) {
                        break;
                    }

                    currentMat = newMat;
                    if (currentMesh != null && UtilClientReflection.getModelMeshMat(currentMesh) == null && UtilClientReflection.getModelMeshFaces(currentMesh).isEmpty()) {
                        UtilClientReflection.setModelMeshMat(currentMesh, currentMat);
                        break;
                    }

                    currentMesh = null;
                    break;
                case "v":
                    ((ObjModelAccess)model).getPositions().add(UtilParse.parseVector4To3(line));
                    break;
                case "vt":
                    ((ObjModelAccess)model).getTexCoords().add(UtilParse.parseVector2(line));
                    break;
                case "vn":
                    ((ObjModelAccess)model).getNormals().add(UtilParse.parseVector3(line));
                    break;
                case "vc":
                    ((ObjModelAccess)model).getColors().add(UtilParse.parseVector4(line));
                    break;
                case "f":
                    if (currentMesh == null) {
                        Objects.requireNonNull(model);
                        currentMesh = UtilClientReflection.createNewModelMesh(model, currentMat, currentSmoothingGroup);
                        if (currentObject != null) {
                            ((ModelObjectAccess) currentObject).getMeshes().add(currentMesh);
                        } else {
                            if (currentGroup == null) {
                                Objects.requireNonNull(model);
                                currentGroup = UtilClientReflection.createModelGroup(model, "");
                                ((ObjModelAccess)model).getParts().put("", currentGroup);
                            }

                            ((ModelObjectAccess) currentGroup).getMeshes().add(currentMesh);
                        }
                    }

                    int[][] vertices = new int[line.length - 1][];

                    for (int i = 0; i < vertices.length; ++i) {
                        String vertexData = line[i + 1];
                        String[] vertexParts = vertexData.split("/");
                        int[] vertex = Arrays.stream(vertexParts).mapToInt(
                                (num) -> Strings.isNullOrEmpty(num) ? 0 : Integer.parseInt(num)).toArray();
                        if (vertex[0] < 0) {
                            vertex[0] += ((ObjModelAccess)model).getPositions().size();
                        } else {
                            vertex[0]--;
                        }

                        if (vertex.length > 1) {
                            if (vertex[1] < 0) {
                                vertex[1] += ((ObjModelAccess)model).getTexCoords().size();
                            } else {
                                vertex[1]--;
                            }

                            if (vertex.length > 2) {
                                if (vertex[2] < 0) {
                                    vertex[2] += ((ObjModelAccess)model).getNormals().size();
                                } else {
                                    vertex[2]--;
                                }

                                if (vertex.length > 3) {
                                    if (vertex[3] < 0) {
                                        vertex[3] += ((ObjModelAccess)model).getColors().size();
                                    } else {
                                        vertex[3]--;
                                    }
                                }
                            }
                        }

                        vertices[i] = vertex;
                    }

                    UtilClientReflection.getModelMeshFaces(currentMesh).add(vertices);
                    break;
                case "s":
                    lib = "off".equals(line[1]) ? null : line[1];
                    if (Objects.equals(currentSmoothingGroup, lib)) {
                        break;
                    }

                    currentSmoothingGroup = lib;
                    if (currentMesh != null && UtilClientReflection.getModelMeshSmoothingGroup(currentMesh) == null && UtilClientReflection.getModelMeshFaces(currentMesh).isEmpty()) {
                        UtilClientReflection.setModelMeshSmoothingGroup(currentMesh, currentSmoothingGroup);
                        break;
                    }

                    currentMesh = null;
                    break;
                case "g":
                    String groupName = line[line.length-1];
                    Objects.requireNonNull(model);
                    if (((ObjModelAccess)model).getParts().containsKey(groupName)) {
                        currentGroup = ((ObjModelAccess) model).getParts().get(groupName);
                    } else {
                        currentGroup = UtilClientReflection.createModelGroup(model, groupName);
                        ((ObjModelAccess)model).getParts().put(groupName, currentGroup);
                    }
                    for (int i = line.length-2; i >= 1; --i) {
                        groupName = line[i];
                        ObjModel.ModelGroup prevGroup = currentGroup;
                        if (((ModelGroupAccess)prevGroup).getParts().containsKey(groupName)) {
                            currentGroup = (ObjModel.ModelGroup) ((ModelGroupAccess)prevGroup).getParts().get(groupName);
                        } else {
                            currentGroup = UtilClientReflection.createModelGroup(model, groupName);
                        }
                        ((ModelGroupAccess)prevGroup).getParts().put(groupName, currentGroup);
                    }
                    currentObject = null;
                    currentMesh = null;
                    break;
                case "o":
                    lib = line[1];
                    Objects.requireNonNull(model);
                    if (currentGroup != null) {
                        currentObject = UtilClientReflection.createModelObject(model, lib);
                        ((ModelGroupAccess) currentGroup).getParts().put(lib, currentObject);
                    } else {
                        currentObject = UtilClientReflection.createModelGroup(model, lib);
                        ((ObjModelAccess)model).getParts().put(lib, (ObjModel.ModelGroup) currentObject);
                    }
                    currentMesh = null;
                    break;
            }
        }
        return model;
    }

}