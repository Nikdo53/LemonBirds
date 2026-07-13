package net.nikdo53.lemonbirds.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockBehaviour.Properties.class)
public class NoSuffocatingMixin {

    @WrapMethod(method = "isSuffocating")
    public BlockBehaviour.Properties isSuffocatingWrap(BlockBehaviour.StatePredicate isSuffocating, Operation<BlockBehaviour.Properties> original) {
        BlockBehaviour.StatePredicate newPredicate = (state, blockGetter, pos) -> true;
        return original.call(newPredicate);
    }


}
