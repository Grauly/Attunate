package grauly.attunate.rendering

import com.mojang.blaze3d.systems.RenderSystem
import grauly.attunate.Attunate
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.RenderPhase
import net.minecraft.client.render.VertexConsumer
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import java.awt.Color

object RenderHelper {
    fun renderBeam(
        from: Vec3d,
        to: Vec3d,
        beamWidth: Double = 0.2,
        beamEtchPosition: Double = 0.2,
        beamEtchWidth: Double = 0.4,
        beamColor: Color = Color(1f, 0f, 1f, 1f),
        buffer: BufferBuilder,
        ctx: WorldRenderContext
    ) {
        val color: Int = 0xffffff
        val beamDelta = to.subtract(from)
        val camPos = ctx.camera().pos
        var beamNormal = from.subtract(camPos)
        val localUp = beamNormal.crossProduct(beamDelta).normalize()
        val beamUp = localUp.multiply(beamWidth / 2)
        //val beamUp = Vec3d(0.0,beamWidth,0.0)
        val etchUp = localUp.multiply(beamEtchWidth / 2)
        //val etchUp = Vec3d(0.0, beamEtchWidth, 0.0)
        val beamCenter = to.subtract(beamDelta.multiply(beamEtchPosition))
        val positionMatrix = ctx.matrixStack().peek().positionMatrix

        //beamNormal = beamNormal.multiply(-1.0)

        //buffer.fixedColor(255, 0, 255, 100)
        beamVertAtVec(beamCenter, positionMatrix, buffer, camPos, beamNormal)
            .texture(beamEtchPosition.toFloat(), 0.5f)
            .next()
        beamVertAtVec(beamCenter.add(etchUp), positionMatrix, buffer, camPos, beamNormal)
            .texture(beamEtchPosition.toFloat(), 0f)
            .next()
        beamVertAtVec(to.add(beamUp), positionMatrix, buffer, camPos, beamNormal)
            .texture(0f, 0f)
            .next()
        beamVertAtVec(to, positionMatrix, buffer, camPos, beamNormal)
            .texture(0f, 0.5f)
            .next()
        beamVertAtVec(to.subtract(beamUp), positionMatrix, buffer, camPos, beamNormal)
            .texture(0f, 1f)
            .next()
        beamVertAtVec(beamCenter.subtract(etchUp), positionMatrix, buffer, camPos, beamNormal)
            .texture(beamEtchPosition.toFloat(), 1f)
            .next()
        beamVertAtVec(from.subtract(beamUp), positionMatrix, buffer, camPos, beamNormal)
            .texture(1f, 1f)
            .next()
        beamVertAtVec(from, positionMatrix, buffer, camPos, beamNormal)
            .texture(1f, 0.5f)
            .next()
        beamVertAtVec(from.add(beamUp), positionMatrix, buffer, camPos, beamNormal)
            .texture(1f, 0f)
            .next()
        beamVertAtVec(beamCenter.add(etchUp), positionMatrix, buffer, camPos, beamNormal)
            .texture(beamEtchPosition.toFloat(), 0f)
            .next()

        buffer.unfixColor()
        RenderSystem.enableDepthTest()
        RenderSystem.setShader(GameRenderer::getRenderTypeTranslucentProgram)
        RenderSystem.setShaderTexture(0, Identifier.of(Attunate.MODID, "textures/misc/beam.png"))
    }

    fun beamVertAtVec(
        pos: Vec3d,
        matrix: Matrix4f,
        buffer: BufferBuilder,
        camPos: Vec3d,
        beamNormal: Vec3d
    ): VertexConsumer = vertAtVec(pos, matrix, buffer, camPos)
            .light(15, 15)
            .normal(beamNormal.getX().toFloat(), beamNormal.getY().toFloat(), beamNormal.getZ().toFloat())
            .color(1f,0f,1f,.5f)

    fun vertAtVec(pos: Vec3d, matrix: Matrix4f, buffer: BufferBuilder, camPos: Vec3d): VertexConsumer {
        val wPos = pos.subtract(camPos)
        return buffer.vertex(matrix, wPos.getX().toFloat(), wPos.getY().toFloat(), wPos.getZ().toFloat())
    }
}