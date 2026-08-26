package net.nikdo53.lemonbirds.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.tinymultiblocklib.blockentities.AbstractMultiBlockEntity;
import org.jetbrains.annotations.Nullable;

public class BombFallingBirdBlock extends FallingBirdBlock{
    public BombFallingBirdBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable AbstractMultiBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BombFallingBirdBlockEntity(pos, state);
    }

    @Override
    public RenderShape getMultiblockRenderShape(BlockState state, boolean isCenter) {
        return isCenter ? (state.getValue(DESPAWNS) ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.MODEL) : RenderShape.INVISIBLE;
    }
}
