package grauly.attunate.rendering.beams

import com.mojang.blaze3d.systems.RenderSystem
import grauly.attunate.Attunate
import grauly.attunate.rendering.Shaders
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.util.Identifier

class BeamRenderer {
    private val currentBeams = mutableSetOf<BeamAnimator>()

    init {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderBeams)
    }

    private fun renderBeams(ctx: WorldRenderContext) {
        ctx.matrixStack().push()

        RenderSystem.enableDepthTest()
        RenderSystem.enableBlend()
        RenderSystem.setShader { Shaders.BEAM_SHADER }
        RenderSystem.setShaderTexture(0, Identifier.of(Attunate.MODID, "textures/misc/beam.png"))

        val buffer = Tessellator.getInstance().buffer
        //Yes, technically less efficient vertex wise, but the only real way to handle all the beams in one draw call
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR)

        currentBeams.forEach { beam ->
            beam.render(ctx, buffer)
        }
        currentBeams.removeIf { beam -> beam.done() }

        ctx.matrixStack().pop()
        Tessellator.getInstance().draw()
    }

    fun addBeam(beam: BeamAnimator) {
        currentBeams.add(beam)
    }
}