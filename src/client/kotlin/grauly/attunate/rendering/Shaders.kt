package grauly.attunate.rendering

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.ShaderProgram
import net.minecraft.client.render.VertexFormats

object Shaders {
    val BEAM_SHADER = ShaderProgram(
        MinecraftClient.getInstance().resourceManager,
        "attunate_beam",
        VertexFormats.POSITION_TEXTURE_COLOR
    )
}