package grauly.attunate.networking

import grauly.attunate.AttunateClient
import grauly.attunate.rendering.beams.Beam
import grauly.attunate.rendering.beams.BeamAnimator
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf

object ShotBeam {
    fun handle(
        minecraftClient: MinecraftClient,
        clientPlayNetworkHandler: ClientPlayNetworkHandler,
        packetByteBuf: PacketByteBuf,
        packetSender: PacketSender
    ) {
        val data = ShotBeamPacket.deserializeShot(packetByteBuf)
        val beamDelta = data.to.subtract(data.from)
        val etchPosition = beamDelta.normalize().multiply(2.0)
        val beamPoints = listOf(
            SpawnBeamPacket.BeamPoint(data.from, 0.0),
            SpawnBeamPacket.BeamPoint(etchPosition),
            SpawnBeamPacket.BeamPoint(data.from, 0.0)
        )
        val beam = Beam(beamPoints, data.color)
        AttunateClient.beamRenderer.addBeam(BeamAnimator(beam, BeamAnimator.SHOT_BASE_WIDTH, BeamAnimator.SHOT_BASE_LENGTH, 1.0))
    }
}