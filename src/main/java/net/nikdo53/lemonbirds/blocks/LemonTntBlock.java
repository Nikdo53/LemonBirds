package net.nikdo53.lemonbirds.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.nikdo53.tinymultiblocklib.block.BaseMultiblock;
import org.jetbrains.annotations.Nullable;

public class LemonTntBlock extends Block implements EntityBlock {
    public static final BooleanProperty IS_PRIMED = BooleanProperty.create("is_primed");
    public LemonTntBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(IS_PRIMED, false));
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (player.isCreative() || player.isCrouching()){
            level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (newState.isEmpty() && state.getValue(IS_PRIMED)){
            explode(level, pos);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.FLINT_AND_STEEL)){
            level.setBlockAndUpdate(pos, state.setValue(IS_PRIMED, true));
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    private static void explode(Level level, BlockPos pos){
        if (level.isClientSide) return;

        level.explode(
                null,
                Explosion.getDefaultDamageSource(level, null),
                null,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                4.0F,
                false,
                Level.ExplosionInteraction.TNT
        );
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BadPigBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(IS_PRIMED);
    }
}
