package net.nikdo53.lemonbirds.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.nikdo53.lemonbirds.init.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Slot.class)
public abstract class SlotMixin {

    @Shadow
    public abstract ItemStack getItem();

    @WrapMethod(method = "mayPickup")
    public boolean mayPickup(Player player, Operation<Boolean> original) {
        if (this.getItem().is(ModItems.EMPTY_ITEM.get())){
            return false;
        }
        return original.call(player);
    }

    @WrapMethod(method = "isHighlightable")
    public boolean isHighlightable(Operation<Boolean> original) {
        if (this.getItem().is(ModItems.EMPTY_ITEM.get())){
            return false;
        }
        return original.call();
    }

}
