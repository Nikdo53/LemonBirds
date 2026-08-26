package net.nikdo53.lemonbirds.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.nikdo53.lemonbirds.init.ModBlocks;
import net.nikdo53.lemonbirds.init.ModParticles;
import net.nikdo53.tinymultiblocklib.block.AbstractMultiBlock;
import net.nikdo53.tinymultiblocklib.block.IMultiBlock;
import net.nikdo53.tinymultiblocklib.blockentities.AbstractMultiBlockEntity;
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
    public @Nullable AbstractMultiBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
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

    public SimpleParticleType getParticle(){
        if (this == ModBlocks.RED_LEMON_BLOCK.get()){
            return ModParticles.FEATHER_RED.get();
        } else if (this == ModBlocks.BLUE_LEMON_BLOCK.get()){
            return ModParticles.FEATHER_BLUE.get();
        } else if (this == ModBlocks.BOMB_LEMON_BLOCK.get()){
            return ModParticles.FEATHER_BOMB.get();
        } else if (this == ModBlocks.YELLOW_LEMON_BLOCK.get()){
            return ModParticles.FEATHER_YELLOW.get();
        } else if (this == ModBlocks.WHITE_LEMON_BLOCK.get()){
            return ModParticles.FEATHER_WHITE.get();
        } else if (this == ModBlocks.BIG_LEMON_BLOCK.get()){
            return ModParticles.FEATHER_BIG.get();
        } else {
            return ModParticles.FEATHER_RED.get();
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);

        if (!(level instanceof ServerLevel serverLevel)) return;
        RandomSource random = level.getRandom();
        float x = random.nextFloat() ;
        float y = random.nextFloat() ;
        float z = random.nextFloat() ;

        serverLevel.sendParticles(getParticle(), pos.getX() + 0.5f, pos.getY()+ 0.5f, pos.getZ()+ 0.5f,  10,x,y,z, 0.04);

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
