package grauly.attunate.rendering.beams

import grauly.attunate.Attunate
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.render.BufferBuilder
import net.minecraft.util.math.MathHelper
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
        beamPoints.reduce { accumulate: BeamPoint, current: BeamPoint ->
            val segmentDelta = current.pos.subtract(accumulate.pos)
            val localUp = beamNormal.crossProduct(segmentDelta).normalize()
            beamSegment(current, accumulate, localUp, ctx, buffer)
            current
        }
    }

    fun createSlice(anchor: Double, factor: Double): Beam {
        val beamLength = length()
        val anchorDistance = beamLength * anchor
        val lateCutoff = anchorDistance * factor
        val earlyCutoff = (beamLength - anchorDistance) * factor
        val earlySegment = createLerpSegment(earlyCutoff)
        val lateSegment = createLerpSegment(lateCutoff)
        val sliceBeamSegments = beamPoints
            .dropLast(beamPoints.size - if(lateSegment != null) {lateSegment.first + 1} else {beamPoints.size} )
            .drop(if(earlySegment != null) {earlySegment.first + 1} else 0)
            .toMutableList()
        if (earlySegment != null) sliceBeamSegments.add(0, earlySegment.second)
        if (lateSegment != null) sliceBeamSegments.add(lateSegment.second)
        return Beam(sliceBeamSegments, beamColor, widthMultiplier)
    }

    private fun createLerpSegment(beamPos: Double): Pair<Int, BeamPoint>? {
        var distance = 0.0
        var index = 0
        beamPoints.reduce { previous: BeamPoint, current: BeamPoint ->
            val beamDelta = current.pos.subtract(previous.pos)
            val segmentLength = beamDelta.length()
            if (distance + segmentLength < beamPos) {
                distance += segmentLength
                index++
                return@reduce current
            }
            val segmentFac = (beamPos - distance) / segmentLength
            return Pair(index, lerpSegment(previous, current, segmentFac))
        }
        return null
    }

    private fun findSegmentLerp(beamPointFac: Double): Double {
        var length = 0.0
        beamPoints.subList(1, beamPoints.size)
            .fold(beamPoints.first()) { accumulate: BeamPoint, current: BeamPoint ->
                val segmentDelta = current.pos.subtract(accumulate.pos)
                val segmentLength = segmentDelta.length()
                if (length + segmentLength > beamPointFac) {
                    //now figure out the segment fac
                    val segmentFac = beamPointFac - length
                    return segmentFac / segmentLength
                }
                length += segmentLength
                current
            }
        return 0.0
    }

    private fun findBoundPoints(distance: Double): Pair<BeamPoint, BeamPoint> {
        var length = 0.0
        beamPoints.subList(1, beamPoints.size)
            .fold(beamPoints.first()) { accumulate: BeamPoint, current: BeamPoint ->
                val segmentDelta = current.pos.subtract(accumulate.pos)
                val segmentLength = segmentDelta.length()
                if (length + segmentLength > distance) return Pair(accumulate, current)
                length += segmentLength
                current
            }
        return Pair(beamPoints.last(), beamPoints.last())
    }

    fun lerpSegment(a: BeamPoint, b: BeamPoint, fac: Double): BeamPoint {
        val pos = a.pos.lerp(b.pos, fac)
        val width = MathHelper.lerp(fac, a.width, b.width)
        val color = lerpColorRGBA((a.color ?: beamColor), (b.color ?: beamColor), fac)
        return BeamPoint(pos, width, color)
    }

    private fun lerpColorRGBA(from: Color, to: Color, fac: Double): Color {
        val r = lerpInt(fac, from.red, to.red)
        val g = lerpInt(fac, from.green, to.green)
        val b = lerpInt(fac, from.blue, to.blue)
        val a = lerpInt(fac, from.alpha, to.alpha)
        return Color(r, g, b, a)
    }

    private fun lerpColorHSVA(from: Color, to: Color, fac: Double): Color {
        val hsvColorFrom = toHSV(from)
        val hsvColorTo = toHSV(to)
        val h = lerpHue(hsvColorFrom.h, hsvColorTo.h, fac.toFloat())
        val s = MathHelper.lerp(fac.toFloat(), hsvColorFrom.s, hsvColorTo.s)
        val v = MathHelper.lerp(fac.toFloat(), hsvColorFrom.v, hsvColorTo.v)
        val interpolated = toRGB(HSVColor(h, s, v))
        val a = lerpInt(fac, from.alpha, to.alpha)
        return Color(interpolated.red, interpolated.green, interpolated.blue, a)
    }

    private fun lerpHue(from: Float, to: Float, fac: Float): Float {
        if (from == to) return from
        if (from > to) return lerpHue(to, from, fac)
        val maxCCW = to - from
        val maxCW = (from + 1) - to
        var h = if (maxCW <= maxCCW) {
            to + maxCW * fac
        } else {
            to - maxCCW * fac
        }
        if (h < 0) h += 1
        if (h > 1) h -= 1
        return h
    }

    private fun toHSV(color: Color): HSVColor {
        val hsvvals = Color.RGBtoHSB(color.red, color.green, color.blue, null)
        return HSVColor(hsvvals[0], hsvvals[1], hsvvals[2])
    }

    private fun toRGB(color: HSVColor): Color {
        return Color.getHSBColor(color.h, color.s, color.v)
    }

    private fun lerpInt(fac: Double, a: Int, b: Int): Int {
        if (a == b) return a
        if (b >= a) return lerpInt(1-fac, b, a)
        return a - ((a - b) * fac).toInt()
    }

    private fun length(): Double {
        var length = 0.0
        beamPoints.subList(1, beamPoints.size)
            .fold(beamPoints.first()) { accumulate: BeamPoint, current: BeamPoint ->
                length += accumulate.pos.distanceTo(current.pos)
                current
            }
        return length
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
data class HSVColor(val h: Float, val s: Float, val v: Float)
