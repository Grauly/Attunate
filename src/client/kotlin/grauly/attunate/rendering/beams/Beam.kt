package grauly.attunate.rendering.beams

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.OverlayTexture
import net.minecraft.util.math.Vec3d
import java.awt.Color
import java.security.InvalidParameterException

class Beam(
    private val beamPoints: List<BeamPoint>,
    private val beamColor: Color,
) {
    init {
        if (beamPoints.size < 2) throw InvalidParameterException("must specify at least 2 beam points")
    }

    fun render(ctx: WorldRenderContext, buffer: BufferBuilder) {
        val camPos = ctx.camera().pos
        val beamNormal = beamPoints[0].pos.subtract(camPos)
        beamPoints.foldRight(beamPoints[0]) { current, last ->
            val segmentDelta = last.pos.subtract(current.pos)
            val localUp = beamNormal.crossProduct(segmentDelta).normalize()
            beamSegment(last, current, localUp, ctx, buffer)
            current
        }
    }

    private fun beamSegment(from: BeamPoint, to: BeamPoint, up: Vec3d, ctx: WorldRenderContext, buffer: BufferBuilder) {
        fixBufferColor(buffer, from.color ?: beamColor)
        beamVertex(from.pos.subtract(up.multiply(from.width)), buffer, ctx, 0f, 1f)
        beamVertex(from.pos.add(up.multiply(from.width)), buffer, ctx, 0f, 0f)
        fixBufferColor(buffer, from.color ?: beamColor)
        beamVertex(to.pos.add(up.multiply(to.width)), buffer, ctx, 1f, 0f)
        beamVertex(to.pos.subtract(up.multiply(to.width)), buffer, ctx, 1f, 1f)
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
            .overlay(OverlayTexture.DEFAULT_UV)
            .next()
    }
}

data class BeamPoint(val pos: Vec3d, val width: Double, val color: Color? = null)
