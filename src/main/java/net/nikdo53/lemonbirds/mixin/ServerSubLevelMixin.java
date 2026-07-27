package net.nikdo53.lemonbirds.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerSubLevel.class)
public class ServerSubLevelMixin {

    @Expression("? < ?")
    @WrapOperation(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean onTick(double left, double right, Operation<Boolean> original){
        Boolean call = original.call(left, right);
        if (call) {
            System.out.println("left = " + left);
            System.out.println("right = " + right);
        }
        return call;
    }
}
