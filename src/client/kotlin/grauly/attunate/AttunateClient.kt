package grauly.attunate

import grauly.attunate.networking.ClientNetworking
import grauly.attunate.rendering.beams.BeamRenderer
import net.fabricmc.api.ClientModInitializer

object AttunateClient : ClientModInitializer {

    val beamRenderer = BeamRenderer()

    override fun onInitializeClient() {
        ClientNetworking.init()
    }
}