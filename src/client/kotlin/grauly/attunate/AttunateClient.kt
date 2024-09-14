package grauly.attunate

import grauly.attunate.rendering.beams.Beam
import grauly.attunate.rendering.beams.BeamAnimator
import grauly.attunate.rendering.beams.BeamPoint
import grauly.attunate.rendering.beams.BeamRenderer
import net.fabricmc.api.ClientModInitializer
import net.minecraft.util.math.Vec3d
import java.awt.Color
import kotlin.math.sin

object AttunateClient : ClientModInitializer {
    override fun onInitializeClient() {
        val beamRenderer = BeamRenderer()
        beamRenderer.addBeam(
            BeamAnimator(
                Beam(
                    listOf(
                        BeamPoint(Vec3d(0.0, 97.0, 0.0), 0.1),
                        BeamPoint(Vec3d(8.0, 97.0, 0.0), 0.2, Color(0, 0, 255)),
                        BeamPoint(Vec3d(10.0, 97.0, 0.0), 0.1)
                    ), Color(1f, 0f, 0f)
                ),
                { t -> .5 * sin(t) + 1 },
                { t -> Pair(1.0, .5 * sin(t) + 1) },
                Double.POSITIVE_INFINITY
            )
        )
    }
}