package com.onewhohears.onewholibs.util;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class UtilPacket {

    public static void writeVec3(Vec3 vec, FriendlyByteBuf buffer) {
        buffer.writeFloat((float)vec.x());
        buffer.writeFloat((float)vec.y());
        buffer.writeFloat((float)vec.z());
    }

    public static Vec3 readVec3(FriendlyByteBuf buffer) {
        double x = buffer.readFloat();
        double y = buffer.readFloat();
        double z = buffer.readFloat();
        return new Vec3(x, y, z);
    }

}
