package net.nikdo53.lemonbirds.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BombFallingBirdBlock extends FallingBirdBlock{
    public BombFallingBirdBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BombFallingBirdBlockEntity(pos, state);
    }

    @Override
    public RenderShape getMultiblockRenderShape(BlockState state, boolean isCenter) {
        return isCenter ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.INVISIBLE;
    }
}
