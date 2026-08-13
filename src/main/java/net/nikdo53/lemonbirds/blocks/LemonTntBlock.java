package net.nikdo53.lemonbirds.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class LemonTntBlock extends TntBlock {
    public LemonTntBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
        wasExploded(level, pos, explosion);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (player.isCreative() || player.isCrouching()){
            level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (newState.isEmpty() && level.getEntities(EntityTypeTest.forClass(PrimedTnt.class), AABB.encapsulatingFullBlocks(pos, pos), e -> true).isEmpty()){
            explode(level, pos);
        }
    }
}
