package grauly.attunate.rendering.beams

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.render.BufferBuilder
import net.minecraft.util.math.Vec3d
import java.awt.Color
import java.security.InvalidParameterException

open class Beam(
    private val beamPoints: List<BeamPoint>,
    private val beamColor: Color,
    var widthMultiplier: Double = 1.0
) {
    init {
        if (beamPoints.size < 2) throw InvalidParameterException("must specify at least 2 beam points")
    }

    fun render(ctx: WorldRenderContext, buffer: BufferBuilder) {
        val camPos = ctx.camera().pos
        val beamNormal = beamPoints.first().pos.subtract(camPos)
        beamPoints.subList(1, beamPoints.size)
            .fold(beamPoints.first()) { accumulate: BeamPoint, current: BeamPoint ->
                val segmentDelta = current.pos.subtract(accumulate.pos)
                val localUp = beamNormal.crossProduct(segmentDelta).normalize()
                beamSegment(current, accumulate, localUp, ctx, buffer)
                current
            }
    }

    open fun pointWidth(point: BeamPoint): Double {
        return point.width * widthMultiplier
    }

    private fun beamSegment(from: BeamPoint, to: BeamPoint, up: Vec3d, ctx: WorldRenderContext, buffer: BufferBuilder) {
        //halving this bc of UV shenanigans
        //upper half
        fixBufferColor(buffer, to.color ?: beamColor)
        beamVertex(to.pos, buffer, ctx, 1f, .5f)
        beamVertex(to.pos.add(up.multiply(pointWidth(to))), buffer, ctx, 1f, 0f)
        fixBufferColor(buffer, from.color ?: beamColor)
        beamVertex(from.pos.add(up.multiply(pointWidth(from))), buffer, ctx, 0f, 0f)
        beamVertex(from.pos, buffer, ctx, 0f, .5f)

        //lower half
        fixBufferColor(buffer, to.color ?: beamColor)
        beamVertex(to.pos.subtract(up.multiply(pointWidth(to))), buffer, ctx, 1f, 1f)
        beamVertex(to.pos, buffer, ctx, 1f, .5f)
        fixBufferColor(buffer, from.color ?: beamColor)
        beamVertex(from.pos, buffer, ctx, 0f, .5f)
        beamVertex(from.pos.subtract(up.multiply(pointWidth(from))), buffer, ctx, 0f, 1f)
        buffer.unfixColor()
    }

    private fun fixBufferColor(buffer: BufferBuilder, color: Color) {
        buffer.fixedColor(color.red, color.green, color.blue, color.alpha)
    }

    private fun beamVertex(
        pos: Vec3d,
        buffer: BufferBuilder,
        ctx: WorldRenderContext,
        u: Float,
        v: Float
    ) {
        val wPos = pos.subtract(ctx.camera().pos)
        buffer.vertex(
            ctx.matrixStack().peek().positionMatrix,
            wPos.getX().toFloat(),
            wPos.getY().toFloat(),
            wPos.getZ().toFloat()
        )
            .texture(u, v)
            .next()
    }
}

data class BeamPoint(val pos: Vec3d, val width: Double = 0.1, val color: Color? = null)
