package grauly.attunate

import grauly.attunate.rendering.RenderHelper
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.util.math.Vec3d

object AttunateClient : ClientModInitializer {
	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		WorldRenderEvents.AFTER_TRANSLUCENT.register { ctx: WorldRenderContext ->
			val buffer: BufferBuilder = Tessellator.getInstance().buffer
			buffer.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL)
			ctx.matrixStack().push()
			RenderHelper.renderBeam(Vec3d(0.0,100.0,0.0), Vec3d(10.0,100.0,0.0), buffer = buffer, ctx = ctx)
			ctx.matrixStack().pop()
			Tessellator.getInstance().draw()
		}
	}
}