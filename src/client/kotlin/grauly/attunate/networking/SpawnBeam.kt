package grauly.attunate.networking

import grauly.attunate.AttunateClient
import grauly.attunate.rendering.beams.Beam
import grauly.attunate.rendering.beams.BeamAnimator
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf

object SpawnBeam {
    fun handle(
        minecraftClient: MinecraftClient,
        clientPlayNetworkHandler: ClientPlayNetworkHandler,
        packetByteBuf: PacketByteBuf,
        packetSender: PacketSender
    ) {
        val beamData = SpawnBeamPacket.deserializeBeam(packetByteBuf)
        val beam = Beam(beamData.first)
        AttunateClient.beamRenderer.addBeam(BeamAnimator(beam, BeamAnimator.CONST_WIDTH, BeamAnimator.CONST_LENGTH, beamData.second))
    }
}