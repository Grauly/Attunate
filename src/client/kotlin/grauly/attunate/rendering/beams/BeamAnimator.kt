package grauly.attunate.rendering.beams

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.render.BufferBuilder
import net.minecraft.util.math.MathHelper
import kotlin.math.abs

class BeamAnimator(
    private val beam: Beam,
    //map a value of 0-1 to a width multiplier
    private val widthAnimation: (Double) -> Double,
    private val lengthAnimation: (Double) -> Pair<Double, Double>,
    private val maxLifeTime: Double
) {
    private var currentLifeTime = 0.0

    fun render(ctx: WorldRenderContext, buffer: BufferBuilder) {
        val lengthAnimationData = lengthAnimation.invoke(currentLifeTime / maxLifeTime)
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
        beam.widthMultiplier = widthAnimation.invoke(currentLifeTime / maxLifeTime)
        beam.render(ctx, buffer)
    }

    fun done(): Boolean = currentLifeTime > maxLifeTime

    companion object {
        val CONST_WIDTH = { _: Double -> 1.0 }
        val LINEAR_WIDTH_FADE = { t: Double -> -abs(2 * t - 1) + 1 }
        val SHOT_BASE_WIDTH = { t: Double ->
            if (t >= 0.0 && t < 0.1) {
                MathHelper.lerp(10 * t, 0.0, 1.0)
            }
            if (t >= 0.1 && t < 0.5) {
                1.0
            }
            if (t >= 0.5) {
                MathHelper.lerp((t - .5) * 2, 1.0, 0.0)
            }
            0.0
        }

        val CONST_LENGTH = { _: Double -> Pair(1.0, 1.0) }
        val SHOT_BASE_LENGTH = { t: Double ->
            if (t >= 0.5) {
                Pair(1.0, MathHelper.lerp((t - .5) * 2, 1.0, 0.0))
            }
            Pair(1.0, 1.0)
        }
    }
}