package grauly.attunate

import grauly.attunate.item.WardensEchoItem
import net.fabricmc.api.ModInitializer
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object Attunate : ModInitializer {
	public const val MODID : String = "attunate"
    public val LOGGER= LoggerFactory.getLogger(MODID)
	val WARDENS_ECHO = WardensEchoItem()

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Hello Fabric world!")
		Registry.register(Registries.ITEM, Identifier(MODID, "wardens_echo"), WARDENS_ECHO)
	}
}