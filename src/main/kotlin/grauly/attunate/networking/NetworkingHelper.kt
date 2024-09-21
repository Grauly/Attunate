package grauly.attunate.networking

import io.netty.buffer.ByteBuf
import net.minecraft.util.math.Vec3d

object NetworkingHelper {

    fun serializeVector(vector: Vec3d, buf: ByteBuf) {
        buf.writeDouble(vector.getX())
        buf.writeDouble(vector.getY())
        buf.writeDouble(vector.getZ())
    }

    fun deserializeVector(buf: ByteBuf): Vec3d {
        return Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble())
    }
}