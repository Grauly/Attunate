package grauly.attunate.item

import grauly.attunate.Attunate
import grauly.attunate.networking.ShotBeamPacket
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class WardensEchoItem : Item(
    FabricItemSettings()
) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
        if(world.isClient()) return TypedActionResult.fail(stack)
        //now I am on server :)
        Attunate.LOGGER.info("Hello from server!")
        return super.use(world, user, hand)
    }
}