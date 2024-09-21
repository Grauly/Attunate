package grauly.attunate.networking

import io.netty.buffer.ByteBuf
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Vec3d
import java.awt.Color

object SpawnBeamPacket {

    fun sendBeam(beam: BeamData, lifetime: Double = 1.0, player: ServerPlayerEntity) {
        ServerPlayNetworking.send(player, NetworkChannels.SPAWN_BEAM_CHANNEL, serializeBeam(beam, lifetime))
    }

    fun serializeBeam(beam: BeamData, lifetime: Double): PacketByteBuf {
        val buf = PacketByteBufs.create()
        //write the beam points
        buf.writeCollection(beam.beamPoints) { buffer, beamPoint ->
            run {
                NetworkingHelper.serializeVector(beamPoint.pos, buf)
                buffer.writeDouble(beamPoint.width)
                //null hack: if true, the following int is a color, if not, 0
                buffer.writeBoolean(beamPoint.color != null)
                buffer.writeInt(beamPoint.color?.rgb ?: 0)
            }
        }
        //write the color
        buf.writeInt(beam.color.rgb)
        buf.writeDouble(lifetime)
        return buf
    }

    fun deserializeBeam(buf: ByteBuf): Pair<BeamData, Double> {
        //first, read the collection size
        val collectionSize = buf.readInt()
        val beamPoints = mutableListOf<BeamPoint>()
        //then read the elements
        for (i in 0..collectionSize) {
            val pos = NetworkingHelper.deserializeVector(buf)
            val width = buf.readDouble()
            //execute the null hack
            val color = if (buf.readBoolean()) Color(buf.readInt()) else {
                buf.readInt(); null
            }
            beamPoints.add(BeamPoint(pos, width, color))
        }
        //put the BeamData back together
        return Pair(BeamData(beamPoints, Color(buf.readInt())), buf.readDouble())
    }


    data class BeamPoint(val pos: Vec3d, val width: Double = 0.1, val color: Color? = null)
    data class BeamData(val beamPoints: List<BeamPoint>, val color: Color)
}