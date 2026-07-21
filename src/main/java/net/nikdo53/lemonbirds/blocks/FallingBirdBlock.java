package net.nikdo53.lemonbirds.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.nikdo53.tinymultiblocklib.block.AbstractMultiBlock;
import net.nikdo53.tinymultiblocklib.block.IMultiBlock;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class FallingBirdBlock extends AbstractMultiBlock {
    public static final BooleanProperty DESPAWNS = BooleanProperty.create("despawns");
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    final Function<BlockPos, List<BlockPos>> shapeFunction;

    public FallingBirdBlock(Properties properties, Function<BlockPos, List<BlockPos>> shapeFunction) {
        super(properties);
        this.shapeFunction = shapeFunction;
        registerDefaultState(this.stateDefinition.any().setValue(DESPAWNS, false));
    }

    public FallingBirdBlock(Properties properties) {
        this(properties, List::of);
    }

    public static final Function<BlockPos, List<BlockPos>> MATILDA_SHAPE = (pos) -> List.of(pos, pos.above());
    public static final Function<BlockPos, List<BlockPos>> TERENCE_SHAPE = (pos) -> IMultiBlock.posStreamToList(BlockPos.betweenClosedStream(pos.north().east(), pos.south().west().above(2)));

    @Override
    public List<BlockPos> makeFullBlockShape(Level level, BlockPos center, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction direction) {
        return shapeFunction.apply(center);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FallingBirdBlockEntity(pos, state);
    }

    @Override
    public RenderShape getMultiblockRenderShape(BlockState state, boolean isCenter) {
        return isCenter ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DESPAWNS);
    }

    @Override
    public @Nullable DirectionProperty getDirectionProperty() {
        return FACING;
    }

    @Override
    public boolean hasCustomBE() {
        return true;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return ((level1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof FallingBirdBlockEntity fallingBirdBlockEntity && state1.getValue(DESPAWNS)) {
                fallingBirdBlockEntity.tick(level1, pos, state1);
            }
        });
    }
}
