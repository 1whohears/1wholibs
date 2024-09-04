package com.onewhohears.onewholibs.util;

import net.minecraftforge.client.model.obj.ObjMaterialLibrary;
import net.minecraftforge.client.model.obj.ObjModel;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UtilClientReflection {

    private static Constructor<ObjModel> objModelConstructor;
    private static Constructor<?> modelMeshConstructor;
    private static Constructor<ObjModel.ModelObject> modelObjectConstructor;
    private static Constructor<ObjModel.ModelGroup> modelGroupConstructor;
    private static Field matField;
    private static Field facesField;
    private static Field smoothingGroupField;

    public static ObjModel createNewObjModel(ObjModel.ModelSettings settings, Map<String, String> deprecationWarnings) {
        if (objModelConstructor == null) {
            objModelConstructor = (Constructor<ObjModel>) ObjModel.class.getDeclaredConstructors()[0];
            objModelConstructor.setAccessible(true);
        }
        try {
            return objModelConstructor.newInstance(settings, deprecationWarnings);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object createNewModelMesh(ObjModel model, @Nullable ObjMaterialLibrary.@Nullable Material currentMat, String currentSmoothingGroup) {
        if (modelMeshConstructor == null) {
            System.out.println("ObjModel declared classes "+Arrays.toString(ObjModel.class.getDeclaredClasses()));
            Class<?> modelMeshClass = ObjModel.class.getDeclaredClasses()[1];
            modelMeshConstructor = modelMeshClass.getDeclaredConstructors()[0];
            modelMeshConstructor.setAccessible(true);
        }
        try {
            return modelMeshConstructor.newInstance(model, currentMat, currentSmoothingGroup);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Field getMatField(Object mesh) {
        if (matField == null) {
            try {
                matField = mesh.getClass().getField("mat");
                matField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        return matField;
    }

    private static Field getFacesField(Object mesh) {
        if (facesField == null) {
            try {
                facesField = mesh.getClass().getField("faces");
                facesField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        return facesField;
    }

    private static Field getSmoothingGroupField(Object mesh) {
        if (smoothingGroupField == null) {
            try {
                smoothingGroupField = mesh.getClass().getField("smoothingGroup");
                smoothingGroupField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        return smoothingGroupField;
    }

    public static ObjMaterialLibrary.Material getModelMeshMat(Object mesh) {
        try {
            return (ObjMaterialLibrary.Material) getMatField(mesh).get(mesh);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setModelMeshMat(Object mesh, ObjMaterialLibrary.Material mat) {
        try {
            getMatField(mesh).set(mesh, mat);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<int[][]> getModelMeshFaces(Object mesh) {
        try {
            return (List<int[][]>) getFacesField(mesh).get(mesh);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getModelMeshSmoothingGroup(Object mesh) {
        try {
            return (String) getSmoothingGroupField(mesh).get(mesh);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setModelMeshSmoothingGroup(Object mesh, String smoothingGroup) {
        try {
            getSmoothingGroupField(mesh).set(mesh, smoothingGroup);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static ObjModel.ModelObject createModelObject(ObjModel model, String name) {
        if (modelObjectConstructor == null) {
            modelObjectConstructor = (Constructor<ObjModel.ModelObject>) ObjModel.ModelObject.class.getDeclaredConstructors()[0];
            modelObjectConstructor.setAccessible(true);
        }
        try {
            return modelObjectConstructor.newInstance(model, name);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static ObjModel.ModelGroup createModelGroup(ObjModel model, String name) {
        if (modelGroupConstructor == null) {
            modelGroupConstructor = (Constructor<ObjModel.ModelGroup>) ObjModel.ModelGroup.class.getDeclaredConstructors()[0];
            modelGroupConstructor.setAccessible(true);
        }
        try {
            return modelGroupConstructor.newInstance(model, name);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
