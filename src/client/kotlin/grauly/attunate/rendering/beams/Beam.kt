package grauly.attunate.rendering.beams

import grauly.attunate.rendering.BeamRenderHelper
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.render.BufferBuilder
import net.minecraft.util.math.Vec3d
import java.awt.Color

class Beam(
    private val from: Vec3d,
    private val to: Vec3d,
    private val beamColor: Color,
    private val widthProfile: BeamWidthProfile = BeamWidthProfile()
) {
    fun render(ctx: WorldRenderContext, buffer: BufferBuilder) {
        BeamRenderHelper.renderBeam(from, to, widthProfile.baseWidth, widthProfile.etchPercentage, widthProfile.etchWidth, beamColor, buffer, ctx)
    }
}

data class BeamWidthProfile(val baseWidth: Double = 0.0, val etchPercentage: Double = 0.2, val etchWidth: Double = 0.1)