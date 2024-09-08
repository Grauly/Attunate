package grauly.attunate.rendering

import com.mojang.blaze3d.systems.RenderSystem
import grauly.attunate.Attunate
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.OverlayTexture
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import java.awt.Color

object BeamRenderHelper {
    fun renderBeam(
        from: Vec3d,
        to: Vec3d,
        beamWidth: Double = 0.2,
        beamEtchPosition: Double = 0.2,
        beamEtchWidth: Double = 0.4,
        beamColor: Color = Color(0f, 1f, 0f, 1f),
        buffer: BufferBuilder,
        ctx: WorldRenderContext
    ) {
        val beamDelta = to.subtract(from)
        val camPos = ctx.camera().pos
        val beamNormal = from.subtract(camPos)
        val localUp = beamNormal.crossProduct(beamDelta).normalize()
        val beamUp = localUp.multiply(beamWidth / 2)
        val etchUp = localUp.multiply(beamEtchWidth / 2)
        val beamCenter = to.subtract(beamDelta.multiply(beamEtchPosition))
        val positionMatrix = ctx.matrixStack().peek().positionMatrix

        buffer.fixedColor(beamColor.red, beamColor.green, beamColor.blue, 255 * 2)

        beamVertex(beamCenter, positionMatrix, buffer, camPos, beamEtchPosition.toFloat(), 0.5f)
        beamVertex(beamCenter.add(etchUp), positionMatrix, buffer, camPos, beamEtchPosition.toFloat(), 0f)
        beamVertex(to.add(beamUp), positionMatrix, buffer, camPos, 0f, 0f)
        beamVertex(to, positionMatrix, buffer, camPos, 0f, 0.5f)
        beamVertex(to.subtract(beamUp), positionMatrix, buffer, camPos, 0f, 1f)
        beamVertex(beamCenter.subtract(etchUp), positionMatrix, buffer, camPos, beamEtchPosition.toFloat(), 1f)
        beamVertex(from.subtract(beamUp), positionMatrix, buffer, camPos, 1f, 1f)
        beamVertex(from, positionMatrix, buffer, camPos, 1f, 0.5f)
        beamVertex(from.add(beamUp), positionMatrix, buffer, camPos, 1f, 0f)
        beamVertex(beamCenter.add(etchUp), positionMatrix, buffer, camPos, beamEtchPosition.toFloat(), 0f)

        buffer.unfixColor()

        RenderSystem.enableDepthTest()
        RenderSystem.enableBlend()
        RenderSystem.setShader { Shaders.BEAM_SHADER }
        RenderSystem.setShaderTexture(0, Identifier.of(Attunate.MODID, "textures/misc/beam.png"))
    }

    private fun beamVertex(
        pos: Vec3d,
        positionMatrix: Matrix4f,
        buffer: BufferBuilder,
        camPos: Vec3d,
        u: Float,
        v: Float
    ) {
        val wPos = pos.subtract(camPos)
        buffer.vertex(positionMatrix, wPos.getX().toFloat(), wPos.getY().toFloat(), wPos.getZ().toFloat())
            .texture(u, v)
            .overlay(OverlayTexture.DEFAULT_UV)
            .next()
    }
}