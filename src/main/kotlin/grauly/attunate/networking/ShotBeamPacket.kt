package grauly.attunate.networking

import io.netty.buffer.ByteBuf
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.Vec3d
import java.awt.Color

object ShotBeamPacket {

    fun serializeShot(from: Vec3d, to: Vec3d, color: Color): PacketByteBuf {
        val buf = PacketByteBufs.create()
        NetworkingHelper.serializeVector(from, buf)
        NetworkingHelper.serializeVector(to, buf)
        buf.writeInt(color.rgb)
        return buf
    }

    fun deserializeShot(buf: ByteBuf): ShotBeamData {
        val from = NetworkingHelper.deserializeVector(buf)
        val to = NetworkingHelper.deserializeVector(buf)
        val color = Color(buf.readInt())
        return ShotBeamData(from, to, color)
    }

}

data class ShotBeamData(val from: Vec3d, val to: Vec3d, val color: Color)