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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.nikdo53.lemonbirds.entities.AbstractLemonBirdEntity;
import net.nikdo53.lemonbirds.init.ModDataAttachments;
import net.nikdo53.lemonbirds.init.ModItems;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class BirdItem extends Item implements ProjectileItem {
    public final BiFunction<Level, Player, AbstractLemonBirdEntity> useFunction;
    public final BiFunction<Level, Position, AbstractLemonBirdEntity> projectileFunction;
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
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        Player player = context.getPlayer();

        if (player != null && player.isCrouching() && player.isCreative() && level.getBlockState(pos).canBeReplaced() && getBlock().isPresent()) {
            level.setBlockAndUpdate(pos, Objects.requireNonNull(getBlock().get().getStateForPlacement(new BlockPlaceContext(context))));
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }

    public float getFlyingSpeed() {
        return this == ModItems.TERENCE_BIRD.get() ? 1.5F : 2.0F;
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
        return armorType == EquipmentSlot.HEAD;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PARROT_FLY, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            AbstractLemonBirdEntity bird = useFunction.apply(level, player);
            bird.setItem(itemstack);
            bird.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, getFlyingSpeed() * 1.5f, 1.0F);
            level.addFreshEntity(bird);
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

    public Vec3 getSlingshotModelOffset(){
        if (this == ModItems.TERENCE_BIRD.get()){
            return new Vec3(0, -1, -1);
        }

        if (this == ModItems.BOMB_BIRD.get()){
            return new Vec3(0, -0.5, -0.5);
        }


        return new Vec3(0, 0, 0);
    }



}
