package net.nikdo53.lemonbirds.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.lemonbirds.init.ModBlockEntities;
import net.nikdo53.tinymultiblocklib.blockentities.AbstractMultiBlockEntity;

public class BadPigBlockEntity extends AbstractMultiBlockEntity {
    public BadPigBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BAD_PIG.get(), pos, state);
    }
}
