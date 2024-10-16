package com.onewhohears.onewholibs.util;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import com.mojang.math.Vector4f;
import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * @author 1whohears
 */
public class UtilParse {
	
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Gson GSON = new Gson();
	
	public static CompoundTag getComoundFromResource(String path) {
		CompoundTag compound;
        DataInputStream dis;
        try {
            dis = new DataInputStream(new GZIPInputStream(getResourceAsStream(path)));
            compound = NbtIo.read(dis);
            dis.close();
        } catch (Exception e) {
        	LOGGER.error("ERROR: COULD NOT PARSE COMPOUNDTAG "+path);
            e.printStackTrace();
        	return new CompoundTag();
        }
        return compound;
	}
	
	public static CompoundTag getCompoundFromJson(JsonObject json) {
		return JsonToNBTUtil.getTagFromJson(json);
	}
	
	public static CompoundTag getCompoundFromJsonResource(String path) {
		return getCompoundFromJson(getJsonFromResource(path));
	}
	
	public static JsonObject getJsonFromResource(Resource resource) {
		JsonObject json;
		try {
			BufferedReader br = resource.openAsReader();
			json = GSON.fromJson(br, JsonObject.class);
			br.close();
		} catch (Exception e) {
			LOGGER.error("ERROR: COULD NOT PARSE JSON "+resource.sourcePackId());
			e.printStackTrace();
			return new JsonObject();
		}
		return json;
	}
	
	public static JsonObject getJsonFromResource(String path) {
		JsonObject json;
        InputStreamReader isr;
        try {
        	isr = new InputStreamReader(getResourceAsStream(path));
            json = GSON.fromJson(isr, JsonObject.class);
            isr.close();
        } catch (Exception e) {
        	LOGGER.error("ERROR: COULD NOT PARSE JSON "+path);
            e.printStackTrace();
        	return new JsonObject();
        }
        return json;
	}

	private static InputStream getResourceAsStream(String resource) {
	    return UtilParse.class.getResourceAsStream(resource);
	}
	
	public static void writeVec3(CompoundTag tag, Vec3 v, String name) {
		CompoundTag vec3 = new CompoundTag();
		vec3.putDouble("x", v.x);
		vec3.putDouble("y", v.y);
		vec3.putDouble("z", v.z);
		tag.put(name, vec3);
	}
	
	public static Vec3 readVec3(CompoundTag tag, String name) {
		double x, y, z;
		if (tag.contains(name, 10)) {
			CompoundTag vec3 = tag.getCompound(name);
			x = vec3.getDouble("x");
			y = vec3.getDouble("y");
			z = vec3.getDouble("z");
		} else {
			x = tag.getDouble(name + "x");
			y = tag.getDouble(name + "y");
			z = tag.getDouble(name + "z");
		}
		return new Vec3(x, y, z);
	}

	public static float fixFloatNbt(CompoundTag nbt, String tag, CompoundTag presetNbt, float min) {
		if (nbt.contains(tag)) {
			float f = nbt.getFloat(tag);
			if (f > min) return f;
		} 
		float nbtf = presetNbt.getFloat(tag);
		nbt.putFloat(tag, nbtf);
		return nbtf;
	}

	public static float fixFloatNbt(CompoundTag nbt, String tag, float alt) {
		if (!nbt.contains(tag)) {
			nbt.putFloat(tag, alt);
			return alt;
		}
		return nbt.getFloat(tag);
	}
	
	public static String prettyVec3(Vec3 v) {
		return prettyVec3(v, 1);
	}
	
	public static String prettyVec3(Vec3 v, int decimals) {
		String f = "%3."+decimals+"f";
		return String.format("["+f+","+f+","+f+"]", v.x, v.y, v.z);
	}

	public static String prettyVec2(Vec2 v) {
		return prettyVec2(v, 1);
	}

	public static String prettyVec2(Vec2 v, int decimals) {
		String f = "%3."+decimals+"f";
		return String.format("["+f+","+f+"]", v.x, v.y);
	}
	
	public static String prettyQ(Quaternion q, int decimals) {
		String f = "%1."+decimals+"f";
		return String.format("["+f+","+f+"i,"+f+"j,"+f+"k]", q.r(), q.i(), q.j(), q.k());
	}
	
	public static String getRandomString(String[]... arrays) {
		int size = 0;
		for (int i = 0; i < arrays.length; ++i) size += arrays[i].length;
		int k = 0, r = (int)(Math.random()*size);
		for (int i = 0; i < arrays.length; ++i) 
			for (int j = 0; j < arrays[i].length; ++j) 
				if (k++ == r) return arrays[i][j];
		return "";
	}
	
	/**
	 * @param weights this array must be the same size as arrays
	 * @param arrays
	 * @return a random string in arrays
	 */
	public static String getRandomString(int[] weights, String[]... arrays) {
		if (weights.length != arrays.length) return "";
		int size = 0;
		for (int i = 0; i < arrays.length; ++i) size += arrays[i].length * weights[i];
		int k = 0, r = (int)(Math.random()*size);
		for (int i = 0; i < arrays.length; ++i) 
			for (int w = 0; w < weights[i]; ++w)
				for (int j = 0; j < arrays[i].length; ++j) 
					if (k++ == r) return arrays[i][j];
		return "";
	}
	
	public static boolean getBooleanSafe(JsonObject json, String name, boolean alt) {
		if (!json.has(name)) return alt;
		return json.get(name).getAsBoolean();
	}
	
	public static int getIntSafe(JsonObject json, String name, int alt) {
		if (!json.has(name)) return alt;
		return json.get(name).getAsInt();
	}
	
	public static float getFloatSafe(JsonObject json, String name, float alt) {
		if (!json.has(name)) return alt;
		return json.get(name).getAsFloat();
	}
	
	public static String getStringSafe(JsonObject json, String name, String alt) {
		if (!json.has(name)) return alt;
		return json.get(name).getAsString();
	}
	
	public static JsonObject getJsonSafe(JsonObject json, String name) {
		if (!json.has(name)) return new JsonObject();
		return json.getAsJsonObject(name);
	}
	
	public static <E extends Enum<E>> E getEnumSafe(JsonObject json, String name, Class<E> enumClass) {
		E[] enums = enumClass.getEnumConstants();
		if (enums.length == 0) return null;
		if (!json.has(name)) return enums[0];
		String enumName = json.get(name).getAsString();
		for (int i = 0; i < enums.length; ++i) 
			if (enums[i].name().equals(enumName)) 
				return enums[i];
		return enums[0];
	}
	
	public static void writeEnum(JsonObject json, String name, Enum<?> value) {
		json.addProperty(name, value.name());
	}
	
	public static String[] getStringArraySafe(JsonObject json, String name) {
		if (!json.has(name)) return new String[0];
		JsonArray ja = json.get(name).getAsJsonArray();
		String[] sa = new String[ja.size()];
		for (int i = 0; i < ja.size(); ++i) sa[i] = ja.get(i).getAsString();
		return sa;
 	}
	
	public static JsonArray stringArrayToJsonArray(String... strings) {
		JsonArray ja = new JsonArray();
		for (int i = 0; i < strings.length; ++i) ja.add(strings[i]);
		return ja;
	}
	
	public static JsonArray resLocArrayToJsonArray(ResourceLocation... locs) {
		JsonArray ja = new JsonArray();
		for (int i = 0; i < locs.length; ++i) ja.add(locs[i].toString());
		return ja;
	}
	
	public static String toColorString(Color color) {
		return Integer.toHexString(0xFF000000 | color.getRGB()).substring(2);
	}
	
	public static Vec3 readVec3(JsonObject json, String name) {
		if (!json.has(name)) return Vec3.ZERO;
		JsonObject vec = json.get(name).getAsJsonObject();
		double x = 0, y = 0, z = 0;
		if (vec.has("x")) x = vec.get("x").getAsDouble();
		if (vec.has("y")) y = vec.get("y").getAsDouble();
		if (vec.has("z")) z = vec.get("z").getAsDouble();
		return new Vec3(x, y, z);
	}
	
	public static void writeVec3(JsonObject json, String name, Vec3 vec) {
		JsonObject v = new JsonObject();
		v.addProperty("x", vec.x);
		v.addProperty("y", vec.y);
		v.addProperty("z", vec.z);
		json.add(name, v);
	}
	
	public static String vec2ToString(Vec2... v) {
		String r = "";
		for (int i = 0; i < v.length; ++i) r += "["+v[i].x+","+v[i].y+"]";
		return r;
	}
	
	public static Vector3f readVec3f(JsonObject json, String name) {
		if (!json.has(name)) return Vector3f.ZERO;
		JsonObject vec = json.get(name).getAsJsonObject();
		float x = 0, y = 0, z = 0;
		if (vec.has("x")) x = vec.get("x").getAsFloat();
		if (vec.has("y")) y = vec.get("y").getAsFloat();
		if (vec.has("z")) z = vec.get("z").getAsFloat();
		return new Vector3f(x, y, z);
	}
	
	public static void writeVec3f(JsonObject json, String name, Vector3f vec) {
		JsonObject v = new JsonObject();
		v.addProperty("x", vec.x());
		v.addProperty("y", vec.y());
		v.addProperty("z", vec.z());
		json.add(name, v);
	}

	public static Vector3f parseVector4To3(String[] line) {
		Vector4f vec4 = parseVector4(line);
		return new Vector3f(vec4.x() / vec4.w(), vec4.y() / vec4.w(), vec4.z() / vec4.w());
	}

	public static Vec2 parseVector2(String[] line) {
		Vec2 var10000;
		switch (line.length) {
			case 1 -> var10000 = new Vec2(0.0F, 0.0F);
			case 2 -> var10000 = new Vec2(Float.parseFloat(line[1]), 0.0F);
			default -> var10000 = new Vec2(Float.parseFloat(line[1]), Float.parseFloat(line[2]));
		}

		return var10000;
	}

	public static Vector3f parseVector3(String[] line) {
		Vector3f var10000;
		switch (line.length) {
			case 1 -> var10000 = new Vector3f(0.0F, 0.0F, 0.0F);
			case 2 -> var10000 = new Vector3f(Float.parseFloat(line[1]), 0.0F, 0.0F);
			case 3 -> var10000 = new Vector3f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), 0.0F);
			default -> var10000 = new Vector3f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]));
		}

		return var10000;
	}

	public static Vector4f parseVector4(String[] line) {
		Vector4f var10000;
		switch (line.length) {
			case 1 -> var10000 = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
			case 2 -> var10000 = new Vector4f(Float.parseFloat(line[1]), 0.0F, 0.0F, 1.0F);
			case 3 -> var10000 = new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), 0.0F, 1.0F);
			case 4 -> var10000 = new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]), 1.0F);
			default -> var10000 = new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]), Float.parseFloat(line[4]));
		}

		return var10000;
	}
	
}
