package net.nikdo53.lemonbirds.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.nikdo53.lemonbirds.items.BirdItem;
import net.nikdo53.tinymultiblocklib.block.AbstractMultiBlock;
import net.nikdo53.tinymultiblocklib.block.IMultiBlock;
import net.nikdo53.tinymultiblocklib.block.IPreviewableMultiblock;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BirdSlingshotBlock extends AbstractMultiBlock implements IPreviewableMultiblock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BirdSlingshotBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getMultiblockRenderShape(BlockState state, boolean isCenter) {
        return isCenter ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.INVISIBLE;
    }

    @Override
    public List<BlockPos> makeFullBlockShape(Level level, BlockPos center, BlockState state, @Nullable BlockEntity blockEntity, Direction direction) {
        BlockPos mid = center.above(3);
        ArrayList<BlockPos> list = new ArrayList<>();
        list.add(center);
        list.add(center.above());
        list.add(center.above(2));
        list.add(mid);

        assert direction != null;
        list.addAll(makeCollumShape(mid, direction.getClockWise()));
        list.addAll(makeCollumShape(mid, direction.getCounterClockWise()));

        return list;
    }

    List<BlockPos> makeCollumShape(BlockPos mid, Direction direction){
        BlockPos end = mid.relative(direction, 2);
        return List.of(mid.relative(direction), end, end.above(), end.above(2), end.above(3));
    }

    @Override
    public @Nullable DirectionProperty getDirectionProperty() {
        return FACING;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BirdSlingshotBlockEntity(pos, state);
    }

    @Override
    public boolean hasCustomBE() {
        return true;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {

        if (level.getBlockEntity(IMultiBlock.getCenter(level, pos)) instanceof BirdSlingshotBlockEntity blockEntity) {


            if (player.isShiftKeyDown() && blockEntity.hasBirdItem() && !blockEntity.isBeingControlled() ) {
                ItemStack birdStack = new ItemStack(blockEntity.birdItem);
                if (!player.getInventory().add(birdStack)) {
                    player.drop(birdStack, false);
                }
                blockEntity.birdItem = null;
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            if (!blockEntity.isBeingControlled() && blockEntity.hasBirdItem()) {
                blockEntity.beginControl(player);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(IMultiBlock.getCenter(level, pos)) instanceof BirdSlingshotBlockEntity blockEntity && stack.getItem() instanceof BirdItem birdItem) {
            if (!blockEntity.hasBirdItem()) {
                blockEntity.birdItem = birdItem;
                return ItemInteractionResult.sidedSuccess(level.isClientSide);

            } else if (birdItem != blockEntity.birdItem) {
                ItemStack birdStack = new ItemStack(blockEntity.birdItem);
                if (!player.getInventory().add(birdStack)) {
                    player.drop(birdStack, false);
                }

                blockEntity.birdItem = birdItem;
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return ((level1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof BirdSlingshotBlockEntity fallingBirdBlockEntity) {
                fallingBirdBlockEntity.tick(level1, pos, state1);
            }
        });

    }
}
