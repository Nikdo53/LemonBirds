package net.nikdo53.lemonbirds.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.nikdo53.tinymultiblocklib.block.AbstractMultiBlock;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class BadPigBlock extends AbstractMultiBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty DAMAGE = IntegerProperty.create("damage", 0, 2);

    public BadPigBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getMultiblockRenderShape(BlockState state, boolean isCenter) {
        return isCenter ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    @Override
    public @Nullable DirectionProperty getDirectionProperty() {
        return FACING;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DAMAGE);
    }

    public static final Function<BlockPos, List<BlockPos>> MATILDA_SHAPE = (pos) -> List.of(pos, pos.above());

    @Override
    public List<BlockPos> makeFullBlockShape(Level level, BlockPos center, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction direction) {
        return List.of(center);
    }
}
