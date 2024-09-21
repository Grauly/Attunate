package grauly.attunate.networking

import io.netty.buffer.ByteBuf
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import java.awt.Color

object ShotBeamPacket {

    fun sendShot(from: Vec3d, to: Vec3d, color: Color, player: ServerPlayerEntity) {
        ServerPlayNetworking.send(player, NetworkChannels.SPAWN_SHOT_BEAM_CHANNEL, serializeShot(from, to, color))
    }

    fun sendShotToAll(from: Vec3d, to: Vec3d, color: Color, serverWorld: ServerWorld) {
        val players = mutableSetOf<ServerPlayerEntity>()
        players.addAll(PlayerLookup.tracking(serverWorld, BlockPos.ofFloored(from.getX(), from.getY(), from.getZ())))
        players.addAll(PlayerLookup.tracking(serverWorld, BlockPos.ofFloored(to.getX(), to.getY(), to.getZ())))
        val packet = serializeShot(from, to, color)
        players.forEach { p -> ServerPlayNetworking.send(p, NetworkChannels.SPAWN_SHOT_BEAM_CHANNEL, packet)}
    }

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