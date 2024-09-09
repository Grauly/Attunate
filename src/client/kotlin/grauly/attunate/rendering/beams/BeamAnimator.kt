package grauly.attunate.rendering.beams

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.render.BufferBuilder

class BeamAnimator(
    private val beam: Beam,
    private val widthAnimation: (Double) -> Double,
    private val maxLifeTime: Double
) {
    private var currentLifeTime = 0.0

    fun render(ctx: WorldRenderContext, buffer: BufferBuilder) {
        beam.widthMultiplier = widthAnimation.invoke(currentLifeTime)
        beam.render(ctx, buffer)
        currentLifeTime += ctx.tickDelta()
    }

    fun done(): Boolean = currentLifeTime > maxLifeTime
}