package net.nikdo53.lemonbirds.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TntBlock.class)
public abstract class TntBlockMixin extends Block {
    @Shadow
    @Deprecated
    public static void explode(Level level, BlockPos pos) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    public TntBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level.getEntities(EntityTypeTest.forClass(PrimedTnt.class), AABB.encapsulatingFullBlocks(pos, pos), e -> true).isEmpty()){
            explode(level, pos);
        }
    }
}
