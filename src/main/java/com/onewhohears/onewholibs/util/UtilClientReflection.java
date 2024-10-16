package com.onewhohears.onewholibs.util;


import net.minecraftforge.client.model.obj.ObjMaterialLibrary;
import net.minecraftforge.client.model.obj.ObjModel;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

    @SuppressWarnings("unchecked")
    public static ObjModel createNewObjModel(ObjModel.ModelSettings settings) {
        initializeObjModelConstructor();
        try {
            // Pass only 'settings' to the constructor as it expects a single argument.
            return objModelConstructor.newInstance(settings);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static void initializeObjModelConstructor() {
        if (objModelConstructor == null) {
            try {
                objModelConstructor = (Constructor<ObjModel>) ObjModel.class.getDeclaredConstructors()[0];
                objModelConstructor.setAccessible(true);
            } catch (SecurityException e) {
                throw new RuntimeException("Failed to access ObjModel constructor", e);
            }
        }
    }

    public static Object createNewModelMesh(ObjModel model, @Nullable ObjMaterialLibrary.@Nullable Material currentMat, String currentSmoothingGroup) {
        initializeModelMeshConstructor();
        try {
            return modelMeshConstructor.newInstance(model, currentMat, currentSmoothingGroup);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initializeModelMeshConstructor() {
        if (modelMeshConstructor == null) {
            try {
                Class<?> modelMeshClass = ObjModel.class.getDeclaredClasses()[1];
                modelMeshConstructor = modelMeshClass.getDeclaredConstructors()[0];
                modelMeshConstructor.setAccessible(true);
            } catch (SecurityException e) {
                throw new RuntimeException("Failed to access ModelMesh constructor", e);
            }
        }
    }

    private static Field getField(Object mesh, String fieldName) {
        try {
            Field field = mesh.getClass().getField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException("Failed to access field: " + fieldName, e);
        }
    }

    public static ObjMaterialLibrary.Material getModelMeshMat(Object mesh) {
        try {
            return (ObjMaterialLibrary.Material) getField(mesh, "mat").get(mesh);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setModelMeshMat(Object mesh, ObjMaterialLibrary.Material mat) {
        try {
            getField(mesh, "mat").set(mesh, mat);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<int[][]> getModelMeshFaces(Object mesh) {
        try {
            return (List<int[][]>) getField(mesh, "faces").get(mesh);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getModelMeshSmoothingGroup(Object mesh) {
        try {
            return (String) getField(mesh, "smoothingGroup").get(mesh);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setModelMeshSmoothingGroup(Object mesh, String smoothingGroup) {
        try {
            getField(mesh, "smoothingGroup").set(mesh, smoothingGroup);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static ObjModel.ModelObject createModelObject(ObjModel model, String name) {
        initializeModelObjectConstructor();
        try {
            return modelObjectConstructor.newInstance(model, name);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static void initializeModelObjectConstructor() {
        if (modelObjectConstructor == null) {
            try {
                modelObjectConstructor = (Constructor<ObjModel.ModelObject>) ObjModel.ModelObject.class.getDeclaredConstructors()[0];
                modelObjectConstructor.setAccessible(true);
            } catch (SecurityException e) {
                throw new RuntimeException("Failed to access ModelObject constructor", e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static ObjModel.ModelGroup createModelGroup(ObjModel model, String name) {
        initializeModelGroupConstructor();
        try {
            return modelGroupConstructor.newInstance(model, name);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static void initializeModelGroupConstructor() {
        if (modelGroupConstructor == null) {
            try {
                modelGroupConstructor = (Constructor<ObjModel.ModelGroup>) ObjModel.ModelGroup.class.getDeclaredConstructors()[0];
                modelGroupConstructor.setAccessible(true);
            } catch (SecurityException e) {
                throw new RuntimeException("Failed to access ModelGroup constructor", e);
            }
        }
    }
}
