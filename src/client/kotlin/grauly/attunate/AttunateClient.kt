package grauly.attunate

import grauly.attunate.rendering.beams.Beam
import grauly.attunate.rendering.beams.BeamRenderer
import net.fabricmc.api.ClientModInitializer
import net.minecraft.util.math.Vec3d
import java.awt.Color

object AttunateClient : ClientModInitializer {
    override fun onInitializeClient() {
        val beamRenderer = BeamRenderer()
        beamRenderer.addBeam(Beam(Vec3d(0.0, 100.0, 0.0), Vec3d(10.0, 100.0, 0.0), Color(1f, 0f, 0f)))
        beamRenderer.addBeam(Beam(Vec3d(10.0, 98.0, 0.0), Vec3d(0.0, 98.0, 0.0), Color(1f, 0f, 1f)))
    }
}