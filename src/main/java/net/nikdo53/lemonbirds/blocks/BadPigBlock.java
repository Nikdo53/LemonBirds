package net.nikdo53.lemonbirds.blocks;

import dev.ryanhcode.sable.api.block.BlockWithSubLevelCollisionCallback;
import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.physics.callback.FragileBlockCallback;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.*;
import net.nikdo53.lemonbirds.init.ModBlocks;
import net.nikdo53.lemonbirds.util.LateTickOperation;
import net.nikdo53.tinymultiblocklib.block.AbstractMultiBlock;
import net.nikdo53.tinymultiblocklib.components.SharedStatePropertiesBuilder;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.List;
import java.util.function.BiFunction;

public class BadPigBlock extends AbstractMultiBlock implements BlockWithSubLevelCollisionCallback {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty DAMAGE = IntegerProperty.create("damage", 0, 2);
    public static final Callback CALLBACK = new Callback();

    final BiFunction<BlockPos, Direction, List<BlockPos>> shapeFunction;

    public BadPigBlock(Properties properties, BiFunction<BlockPos, Direction, List<BlockPos>> shapeFunction) {
        super(properties);
        this.shapeFunction = shapeFunction;
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

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public void createSharedBlockStates(SharedStatePropertiesBuilder builder) {
        super.createSharedBlockStates(builder);
        builder.add(DAMAGE);
    }

    public static final BiFunction<BlockPos, Direction, List<BlockPos>> ONE_BLOCK =
            (pos, direction) -> List.of(pos);

    public static final BiFunction<BlockPos, Direction, List<BlockPos>> FOREMAN_SHAPE =
            (pos, direction) -> List.of(pos, pos.relative(direction.getCounterClockWise()));

    public static final BiFunction<BlockPos, Direction, List<BlockPos>> CHEF_SHAPE =
            (pos, direction) -> List.of(pos, pos.above());

    public static final BiFunction<BlockPos, Direction, List<BlockPos>> KING_SHAPE =
            (pos, direction) -> List.of(pos, pos.relative(direction.getCounterClockWise()), pos.above(), pos.relative(direction.getCounterClockWise()).above());


    @Override
    public BlockState getStateForPlacementHelper(BlockPlaceContext context) {
        return getStateForPlacementHelper(context, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public List<BlockPos> makeFullBlockShape(Level level, BlockPos center, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction direction) {
        return shapeFunction.apply(center, direction);
    }

    @Override
    public Callback sable$getCallback() {
        return CALLBACK;
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 15;
    }

    @Override
    protected float getShadeBrightness(BlockState p_308911_, BlockGetter p_308952_, BlockPos p_308918_) {
        return 1.0F;
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (this == ModBlocks.CHEF_PIG_BOSS.get()){
            return voxelShapeHelper(state, level, pos, Shapes.box(0.0, 0.0, 0.0, 1.0, 2.0, 1.0));
        }
        return super.getShape(state, level, pos, context);
    }

    public static class Callback extends FragileBlockCallback {
        @Override
        public double getTriggerVelocity() {
            return 6.0;
        }

        @Override
        public boolean shouldTriggerFor(BlockState state) {
            return state.getBlock() instanceof BadPigBlock;
        }

        @Override
        public BlockSubLevelCollisionCallback.CollisionResult sable$onCollision(final BlockPos pos, @Nullable final BlockPos otherHitBlockPos, final Vector3d pos1, final double impactVelocity) {
            final double triggerVelocity = this.getTriggerVelocity();

            if (impactVelocity * impactVelocity < triggerVelocity * triggerVelocity) {
                return CollisionResult.NONE;
            }

            final SubLevelPhysicsSystem system = SubLevelPhysicsSystem.getCurrentlySteppingSystem();
            final ServerLevel level = system.getLevel();

            final BlockState state = level.getBlockState(pos);

            if (state.getBlock() instanceof LeavesBlock && state.getValue(LeavesBlock.PERSISTENT))
                return CollisionResult.NONE;

            if (this.shouldTriggerFor(state)) {
                return this.onHitWithVelocity(level, pos, state, impactVelocity);
            }

            return new CollisionResult(JOMLConversion.ZERO, true);
        }


        public CollisionResult onHitWithVelocity(ServerLevel level, BlockPos pos, BlockState state, double velocity) {
            System.out.println("velocity = " + velocity);

            int damageAmount = (int) Math.floor(velocity / getTriggerVelocity());
            if (damageAmount < 1) {
                return CollisionResult.NONE;
            }

            int damage = state.getValue(DAMAGE) + damageAmount;

            if (damage > 2){
                level.destroyBlock(pos, false);
                return new CollisionResult(JOMLConversion.ZERO, true);

            } else {

                LateTickOperation.schedule(level, 1, l -> {
                    level.setBlockAndUpdate(pos, state.setValue(DAMAGE, damage));
                });
            }

            return new CollisionResult(JOMLConversion.ZERO, false);
        }
    }
}
