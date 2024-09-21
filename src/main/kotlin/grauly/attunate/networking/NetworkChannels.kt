package grauly.attunate.networking

import grauly.attunate.Attunate
import net.minecraft.util.Identifier

object NetworkChannels {
    val SPAWN_BEAM_CHANNEL = Identifier(Attunate.MODID, "spawn_beam")
    val SPAWN_SHOT_BEAM_CHANNEL = Identifier(Attunate.MODID, "spawn_shot")
}