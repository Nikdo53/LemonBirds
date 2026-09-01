package net.nikdo53.lemonbirds.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.physics.callback.FragileBlockCallback;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.lemonbirds.init.ModDataMaps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FragileBlockCallback.class)
public class FragileBlockCallbackMixin {

    @WrapOperation(method = "sable$onCollision", at = @At(value = "INVOKE", target = "Ldev/ryanhcode/sable/physics/callback/FragileBlockCallback;getTriggerVelocity()D"))
    public double sable$onCollision(FragileBlockCallback instance, Operation<Double> original, @Local(argsOnly = true, ordinal = 0) BlockPos pos) {
        final SubLevelPhysicsSystem system = SubLevelPhysicsSystem.getCurrentlySteppingSystem();
        final ServerLevel level = system.getLevel();
        BlockState state = level.getBlockState(pos);
        Double data = state.getBlockHolder().getData(ModDataMaps.FRAGILE_OVERRIDES);
        if (data == null)
            return original.call(instance);

        return data;
    }

}
