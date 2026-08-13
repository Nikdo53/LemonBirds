package net.nikdo53.lemonbirds.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.lemonbirds.entities.BombLemonBirdEntity;
import net.nikdo53.lemonbirds.init.ModBlockEntities;
import net.nikdo53.lemonbirds.init.ModBlocks;

public class BombFallingBirdBlockEntity extends FallingBirdBlockEntity {
    public BombFallingBirdBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FALLING_BIRD_BOMB.get() ,pos, blockState);
    }

    @Override
    public int getMaxTicks() {
        return MAX_TICKS / 3;
    }

    @Override
    public void onRemove(Level level, BlockPos pos, BlockState state) {
        BombLemonBirdEntity.birdExplosion(level, pos.getCenter(), null);

        super.onRemove(level, pos, state);
    }
}
