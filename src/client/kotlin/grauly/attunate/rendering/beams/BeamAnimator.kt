package grauly.attunate.rendering.beams

import grauly.attunate.Attunate
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.render.BufferBuilder

class BeamAnimator(
    private val beam: Beam,
    private val widthAnimation: (Double) -> Double,
    private val lengthAnimation: (Double) -> Pair<Double, Double>,
    private val maxLifeTime: Double
) {
    private var currentLifeTime = 0.0

    fun render(ctx: WorldRenderContext, buffer: BufferBuilder) {
        val lengthAnimationData = lengthAnimation.invoke(currentLifeTime)
        renderBeam(
            if (lengthAnimationData.second < 1)
                beam.createSlice(
                    lengthAnimationData.first,
                    lengthAnimationData.second
                )
            else beam,
            ctx,
            buffer
        )
        currentLifeTime += ctx.tickDelta() * 0.05
    }

    private fun renderBeam(beam: Beam, ctx: WorldRenderContext, buffer: BufferBuilder) {
        beam.widthMultiplier = widthAnimation.invoke(currentLifeTime)
        beam.render(ctx, buffer)
    }

    fun done(): Boolean = currentLifeTime > maxLifeTime
}