package net.nikdo53.lemonbirds.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.lemonbirds.entities.BombLemonBirdEntity;
import net.nikdo53.lemonbirds.init.ModBlockEntities;
import net.nikdo53.lemonbirds.init.ModBlocks;
import net.nikdo53.tinymultiblocklib.block.IMultiBlock;
import net.nikdo53.tinymultiblocklib.blockentities.AbstractMultiBlockEntity;

import java.util.Set;

public class FallingBirdBlockEntity extends AbstractMultiBlockEntity {
    public int tickCount = 0;
    public static final int MAX_TICKS = 120;

    public FallingBirdBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FALLING_BIRD.get(), pos, blockState);
    }

    public FallingBirdBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public int getMaxTicks(){
        return MAX_TICKS;
    }

    public void tick(Level level, BlockPos pos, BlockState state){
        if (!isCenter()){
            BlockState centerState = level.getBlockState(getCenter());
            if (!IMultiBlock.isSameMultiblock(level, centerState, state, getCenter(), pos)) {
                level.destroyBlock(pos, false);
                level.removeBlockEntity(pos);
            }
        }
            tickCount++;
            if ((tickCount >= getMaxTicks() && !level.isClientSide())) {
                onRemove(level, pos, state);
            }
    }

    public void onRemove(Level level, BlockPos pos, BlockState state) {
        // Not getFullBlockShapeCache() - inside a sub-level plot nothing ever triggers the neighbour updates
        // that fill the cache, so it can still be empty here and the blocks would never despawn.
        Set<BlockPos> shape = state.getBlock() instanceof IMultiBlock multiBlock
                ? multiBlock.getFullBlockShape(level, pos, state).getGlobalPositions()
                : Set.of(pos);

        for (BlockPos blockPos : shape) {
            level.removeBlock(blockPos, false);
            level.removeBlockEntity(blockPos);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putInt("tickCount", tickCount);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        tickCount = tag.getInt("tickCount");

    }

}
