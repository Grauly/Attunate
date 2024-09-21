package grauly.attunate.networking

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

object ClientNetworking {
    fun init() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkChannels.SPAWN_BEAM_CHANNEL, SpawnBeam::handle)
    }
}