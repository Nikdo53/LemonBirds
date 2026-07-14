package net.nikdo53.lemonbirds.items;

import dev.ryanhcode.sable.api.physics.object.box.BoxPhysicsObject;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.nikdo53.lemonbirds.entities.AbstractLemonBirdEntity;
import net.nikdo53.lemonbirds.init.ModDataAttachments;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class BirdItem extends Item implements ProjectileItem {
    private final BiFunction<Level, Player, AbstractLemonBirdEntity> useFunction;
    private final BiFunction<Level, Position, AbstractLemonBirdEntity> projectileFunction;
    private final Supplier<Block> block;

    public BirdItem(Properties properties, @Nullable Supplier<Block> block, BiFunction<Level, Player, AbstractLemonBirdEntity> useFunction, BiFunction<Level, Position, AbstractLemonBirdEntity> projectileFunction) {
        super(properties);
        this.useFunction = useFunction;
        this.projectileFunction = projectileFunction;
        this.block = block;
    }

    public Optional<Block> getBlock() {
        return Optional.ofNullable(block == null ? null : block.get());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (level.getBlockState(pos).is(Blocks.STONE) && SubLevelPhysicsSystem.get(level) instanceof SubLevelPhysicsSystem system) {

            BoxPhysicsObject object = new BoxPhysicsObject(new Pose3d(new Vector3d(pos.getX(), pos.getY(), pos.getZ()), new Quaterniond(), new Vector3d(), new Vector3d(1)), new Vector3d(2), 0.25);
            system.addObject(object);
        }

        return InteractionResult.FAIL;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PARROT_FLY, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            AbstractLemonBirdEntity bird = useFunction.apply(level, player);
            bird.setItem(itemstack);
            bird.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(bird);

            player.setData(ModDataAttachments.LEMON_BIRD, bird.getId());
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        itemstack.consume(1, player);
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    public AbstractLemonBirdEntity asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        AbstractLemonBirdEntity bird = projectileFunction.apply(level, pos);
        bird.setItem(stack);
        bird.setHasAbility(false);
        return bird;
    }

    public AbstractLemonBirdEntity asProjectile(Level level, Position pos, Direction direction) {
        return asProjectile(level, pos, this.getDefaultInstance(), direction);
    }

}
