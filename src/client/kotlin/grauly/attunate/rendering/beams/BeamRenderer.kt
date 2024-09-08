package grauly.attunate.rendering.beams

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats

class BeamRenderer {
    val currentBeams = mutableSetOf<Beam>()

    init {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderBeams)
    }

    private fun renderBeams(ctx: WorldRenderContext) {
        ctx.matrixStack().push()
        currentBeams.forEach { beam ->
            val buffer = Tessellator.getInstance().buffer
            buffer.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_TEXTURE_COLOR)
            beam.render(ctx, buffer)
            buffer.end()
        }
        ctx.matrixStack().pop()
        Tessellator.getInstance().draw()
    }

    fun addBeam(beam: Beam) {
        currentBeams.add(beam)
    }
}