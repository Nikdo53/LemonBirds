package net.nikdo53.lemonbirds.items;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.lemonbirds.util.LateTickOperation;
import net.nikdo53.lemonbirds.util.LemonUtils;
import net.nikdo53.tinymultiblocklib.block.IMultiBlock;

import java.util.List;
import java.util.Set;

public class SublevelBlockItem extends BlockItem {
    public SublevelBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        BlockState state = this.getBlock().getStateForPlacement(context);

        InteractionResult result = super.place(context);

        if (result.consumesAction() && context.getLevel() instanceof ServerLevel serverLevel) {
            if (getBlock() instanceof IMultiBlock multiBlock) {
                BlockPos pos = context.getClickedPos();
                if (state == null) return InteractionResult.FAIL;

                Set<BlockPos> shape = multiBlock.getMultiblockShapeNoCache(pos, state, context.getLevel(), null).getGlobalPositions();

                LateTickOperation.schedule(serverLevel, 2, (level) ->
                        LemonUtils.assembleIntoSubLevel(level, getBlock(), pos, shape, this.builtInRegistryHolder().getRegisteredName(), null, null));
            }

        }
        return result;
    }
}
