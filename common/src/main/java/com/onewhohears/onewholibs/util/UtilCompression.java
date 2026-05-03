package com.onewhohears.onewholibs.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.network.FriendlyByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class UtilCompression {

    public static void writeCompressedJson(JsonObject json, FriendlyByteBuf buffer) {
        writeCompressedString(json.toString(), buffer);
    }

    public static JsonObject readCompressedJson(FriendlyByteBuf buffer) {
        String json = readCompressedString(buffer);
        return JsonParser.parseString(json).getAsJsonObject();
    }

    public static void writeCompressedString(String msg, FriendlyByteBuf buffer) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(baos);
            gzip.write(msg.getBytes(StandardCharsets.UTF_8));
            gzip.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        buffer.writeByteArray(baos.toByteArray());
    }

    public static String readCompressedString(FriendlyByteBuf buffer) {
        byte[] compressed = buffer.readByteArray();
        GZIPInputStream gzip = null;
        String msg = null;
        try {
            gzip = new GZIPInputStream(new ByteArrayInputStream(compressed));
            msg = new String(gzip.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return msg;
    }

}
