package grauly.attunate.item

import grauly.attunate.Attunate
import grauly.attunate.networking.ShotBeamPacket
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.RaycastContext
import net.minecraft.world.World
import java.awt.Color

class WardensEchoItem : Item(
    FabricItemSettings()
) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
        //haha, funny kotlin "smart" cast
        if (user.itemCooldownManager.isCoolingDown(stack.item)) return TypedActionResult.fail(stack)
        if (world.isClient() || world !is ServerWorld) return TypedActionResult.fail(stack)
        //now I am on server :)
        Attunate.LOGGER.info("Hello from server!")
        val hit = world.raycast(
            RaycastContext(
                user.eyePos,
                user.rotationVector.multiply(50.0),
                RaycastContext.ShapeType.VISUAL,
                RaycastContext.FluidHandling.ANY,
                user
            )
        )
        val hitPos = hit?.pos ?: user.rotationVector.multiply(50.0)
        ShotBeamPacket.sendShotToAll(user.eyePos, hitPos, Color(0, 100, 125), world)
        user.itemCooldownManager.set(stack.item, 10)
        return super.use(world, user, hand)
    }
}