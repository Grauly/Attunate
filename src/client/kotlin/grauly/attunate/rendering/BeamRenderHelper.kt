package grauly.attunate.rendering

import com.mojang.blaze3d.systems.RenderSystem
import grauly.attunate.Attunate
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.render.*
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import org.joml.Matrix3f
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
        val normalMatrix = ctx.matrixStack().peek().normalMatrix

        beamNormal = beamNormal.multiply(-1.0)

        //buffer.fixedColor(255, 0, 255, 100)
        beamVertAtVec(beamCenter, positionMatrix, normalMatrix, buffer, camPos, beamNormal, beamEtchPosition.toFloat(), 0.5f)
            .next()
        beamVertAtVec(beamCenter.add(etchUp), positionMatrix, normalMatrix, buffer, camPos, beamNormal, beamEtchPosition.toFloat(), 0f)
            .next()
        beamVertAtVec(to.add(beamUp), positionMatrix, normalMatrix, buffer, camPos, beamNormal,0f,0f)
            .next()
        beamVertAtVec(to, positionMatrix, normalMatrix, buffer, camPos, beamNormal,0f,0.5f)
            .next()
        beamVertAtVec(to.subtract(beamUp), positionMatrix, normalMatrix, buffer, camPos, beamNormal,0f,1f)
            .next()
        beamVertAtVec(beamCenter.subtract(etchUp), positionMatrix, normalMatrix, buffer, camPos, beamNormal,beamEtchPosition.toFloat(),1f)
            .next()
        beamVertAtVec(from.subtract(beamUp), positionMatrix, normalMatrix, buffer, camPos, beamNormal,1f,1f)
            .next()
        beamVertAtVec(from, positionMatrix, normalMatrix, buffer, camPos, beamNormal,1f,0.5f)
            .next()
        beamVertAtVec(from.add(beamUp), positionMatrix, normalMatrix, buffer, camPos, beamNormal,1f,0f)
            .next()
        beamVertAtVec(beamCenter.add(etchUp), positionMatrix, normalMatrix, buffer, camPos, beamNormal,beamEtchPosition.toFloat(),0f)
            .next()

        RenderSystem.enableDepthTest()
        RenderSystem.enableBlend()
        RenderSystem.setShader { Shaders.BEAM_SHADER }
        RenderSystem.setShaderTexture(0, Identifier.of(Attunate.MODID, "textures/misc/beam.png"))
    }

    fun beamVertAtVec(
        pos: Vec3d,
        positionMatrix: Matrix4f,
        normalMatrix: Matrix3f,
        buffer: BufferBuilder,
        camPos: Vec3d,
        beamNormal: Vec3d,
        u: Float,
        v: Float
    ): VertexConsumer {
        val wPos = pos.subtract(camPos)
        val vert = buffer.vertex(positionMatrix, wPos.getX().toFloat(), wPos.getY().toFloat(), wPos.getZ().toFloat())
            .color(1f, 0f, 1f, 1f)
            .texture(u,v)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
            .normal(normalMatrix, beamNormal.getX().toFloat(), beamNormal.getY().toFloat(), beamNormal.getZ().toFloat())
        return vert
    }
}