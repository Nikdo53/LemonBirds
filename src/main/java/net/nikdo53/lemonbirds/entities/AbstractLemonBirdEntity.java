package net.nikdo53.lemonbirds.entities;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.nikdo53.lemonbirds.blocks.BadPigBlock;
import net.nikdo53.lemonbirds.blocks.BirdSlingshotBlockEntity;
import net.nikdo53.lemonbirds.blocks.FallingBirdBlock;
import net.nikdo53.lemonbirds.init.*;
import net.nikdo53.lemonbirds.items.BirdItem;
import net.nikdo53.lemonbirds.util.LateTickOperation;
import net.nikdo53.lemonbirds.util.LemonUtils;
import net.nikdo53.tinymultiblocklib.block.AbstractMultiBlock;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractLemonBirdEntity extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Boolean> DATA_HAS_ABILITY = SynchedEntityData.defineId(
            AbstractLemonBirdEntity.class, EntityDataSerializers.BOOLEAN
    );

    private static final EntityDataAccessor<Integer> ABILITY_COOLDOWN = SynchedEntityData.defineId(
            AbstractLemonBirdEntity.class, EntityDataSerializers.INT
    );

    private static final EntityDataAccessor<Optional<UUID>> CONTROLLING_PLAYER_ID = SynchedEntityData.defineId(
            AbstractLemonBirdEntity.class, EntityDataSerializers.OPTIONAL_UUID
    );

    public AbstractLemonBirdEntity(EntityType<? extends AbstractLemonBirdEntity> entityType, Level level, Position pos) {
        super(entityType, pos.x(), pos.y(), pos.z(), level);
    }

    public AbstractLemonBirdEntity(EntityType<? extends AbstractLemonBirdEntity> entityType, Level level) {
        super(entityType, level);
    }

    public AbstractLemonBirdEntity(EntityType<? extends AbstractLemonBirdEntity> entityType, Level level, Player player) {
        super(entityType, player, level);
    }

    public abstract DestroyEffectivity getDestroyEffectivity();

    @Override
    protected Item getDefaultItem() {
        return ModItems.RED_LEMON.asItem();
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, inaccuracy);
        Entity owner = getOwner();
        // LEMON_BIRD is a synced attachment, so writing it client side would only overwrite the server's id with
        // one the client made up.
        if (owner instanceof Player player && !level().isClientSide()) {
            player.setData(ModDataAttachments.LEMON_BIRD, getId());
        }
    }

    public void onAbilityKey(float xRot, float yRot){
        if (hasAbility() && entityData.get(ABILITY_COOLDOWN) <= 0) {
            entityData.set(DATA_HAS_ABILITY, false);
            activateAbility(xRot, yRot);
            level().addParticle(ModParticles.LEMON_BIRD_ABILITY.get(), true, getX(), getY() + 0.5, getZ(), 0, 0, 0);
        }
    }

    public void setControllingPlayer(@Nullable Player player) {
        entityData.set(CONTROLLING_PLAYER_ID, Optional.ofNullable(player != null ? player.getUUID() : null));
    }

    public Optional<UUID> getControllingPlayer() {
        return entityData.get(CONTROLLING_PLAYER_ID);
    }

    public boolean isControllingPlayer(@Nullable Player player) {
        return getControllingPlayer().map(uuid -> uuid.equals(player != null ? player.getUUID() : null)).orElse(false);
    }

    protected abstract void activateAbility(float xRot, float yRot);

    public void setHasAbility(boolean hasAbility){
        if (getOwner() != null) {
            entityData.set(DATA_HAS_ABILITY, hasAbility);
        }
    }

    public boolean hasAbility(){
        return getOwner() != null ? entityData.get(DATA_HAS_ABILITY) : false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HAS_ABILITY, true);
        builder.define(ABILITY_COOLDOWN, 5);
        builder.define(CONTROLLING_PLAYER_ID, Optional.empty());
    }


    @Override
    public void tick() {
        super.tick();

        if (entityData.get(ABILITY_COOLDOWN) > 0) {
            entityData.set(ABILITY_COOLDOWN, entityData.get(ABILITY_COOLDOWN) - 1);
        }

        if (level().isClientSide()){
            level().addParticle(ModParticles.LEMON_BIRD_TRAIL.get(), true, getX(), getY() + 0.5, getZ(), 0, 0, 0);
        }

        Entity aimSource = getAimSource();
        if (aimSource != null) {
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
            setRot(aimSource.getYRot(), aimSource.getXRot());
        }

        if (level().isClientSide() && getControllingPlayer().isPresent()) {
            BirdSlingshotBlockEntity.ClientThingy.trySetCamera(this, getControllingPlayer().get());
        }

    }


    @Override
    public void moveTo(double x, double y, double z, float yRot, float xRot) {
        this.setOldPosAndRot();
        this.setPosRaw(x, y, z);
        this.setYRot(yRot);
        this.setXRot(xRot);
        this.reapplyPosition();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        Level level = level();
        BlockPos pos = result.getBlockPos().immutable();
        BirdItem birdItem = (BirdItem) getItem().getItem();
        if (level.getBlockState(pos).is(ModBlocks.SLING_SHOT))
            return;

        Vec3 location = result.getLocation();
        Vec3 normal = location.subtract(pos.getX(), pos.getY(), pos.getZ()).subtract(0.5, 0.5, 0.5).multiply(-1, -1, -1);

        Vec3 movement = getDestroyEffectivity().applyMovementPostHit(this, birdItem, level.getBlockState(pos));
        double speed = 10.0 * movement.lengthSqr();
        double size = 0.1 * speed * (1 / birdItem.getFlyingSpeed() / 2);

        if (birdItem == ModItems.BOMB_LEMON.get() || birdItem == ModItems.BIG_LEMON.get()){
            size++;
        }

        if (level.getBlockState(pos).getBlock() instanceof BadPigBlock pigBlock && level instanceof ServerLevel serverLevel){
            if (getDestroyEffectivity().canDestroy){
                pigBlock.sable$getCallback().onHitWithVelocity(serverLevel, pos, level.getBlockState(pos), getDeltaMovement().lengthSqr() * 5);
            }
        }

        this.setDeltaMovement(movement);

        if (speed > 5 && getDestroyEffectivity().canDestroy) {
            BlockPos.betweenClosedStream(AABB.ofSize(location, size, size, size))
                    .forEach(blockPos -> {
                        if (level.getBlockState(blockPos).is(ModBlockTags.BIRD_BREAKABLE)) {
                            level.destroyBlock(blockPos, false);
                        }
                    });

            entityData.set(ABILITY_COOLDOWN, 1);

        } else {
            if (level instanceof ServerLevel serverLevel) {
                ServerSubLevel subLevel = (ServerSubLevel) SableCompanion.INSTANCE.getContaining(level, pos);
                LemonUtils.applyPhysics(serverLevel, subLevel, normal, speed * 12);
            }

            discard();
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (getOwner() instanceof Player player && !level().isClientSide()) {
            if (player.getData(ModDataAttachments.LEMON_BIRD) == this.getId()) {
                player.removeData(ModDataAttachments.LEMON_BIRD);
            }
        }

        if (level().isClientSide() && getControllingPlayer().isPresent()){
            BirdSlingshotBlockEntity.ClientThingy.resetCamera(getControllingPlayer().get());
        }
        // Only leave a block behind when the bird is actually gone for good, not when it's unloaded or moved between
        // dimensions - otherwise a chunk unload duplicates the block.
        if (reason.shouldDestroy() && !isRemoved()) {
            turnIntoBlock();
        }

        super.remove(reason);
    }

    /**
     * Whoever is aiming this bird. Normally the owner, but Projectile#findOwner gives up on a client level, so a bird
     * launched from a slingshot has no owner client side until the first tick sets one - fall back to the controlling
     * player so the camera and the ability aim are never left following the bird's own stale rotation.
     */
    public @Nullable Entity getAimSource() {
        Entity owner = getOwner();
        if (owner != null) {
            return owner;
        }
        return getControllingPlayer().map(uuid -> (Entity) level().getPlayerByUUID(uuid)).orElse(null);
    }

    @Override
    public float getViewXRot(float partialTicks) {
        Entity aimSource = getAimSource();
        if (aimSource != null){
            return aimSource.getViewXRot(partialTicks);
        }
        return super.getViewXRot(partialTicks);
    }

    @Override
    public float getViewYRot(float partialTick) {
        Entity aimSource = getAimSource();
        if (aimSource != null){
            return aimSource.getViewYRot(partialTick);
        }
        return super.getViewYRot(partialTick);
    }

    public void turnIntoBlock(){
        // Placing the block is server side only - the client gets it from the block update - and a sub-level can only
        // ever be assembled on a ServerLevel.
        if (!(level() instanceof ServerLevel serverLevel)) return;

        Item item = getItem().getItem();
        if (!(item instanceof BirdItem birdItem)) return;

        Optional<Block> block = birdItem.getBlock();
        if (block.isEmpty() || !(block.get() instanceof FallingBirdBlock multiBlock)) return;


        BlockState state = multiBlock.defaultBlockState()
                .setValue(FallingBirdBlock.DESPAWNS, true)
                .setValue(AbstractMultiBlock.CENTER, true)
                .setValue(FallingBirdBlock.FACING, Direction.SOUTH);

        BlockPos pos = blockPosition();
        Set<BlockPos> shape = multiBlock.getMultiblockShapeNoCache(pos, state, serverLevel, null).getGlobalPositions();

        serverLevel.setBlock(pos, state, 3);

        Vec2 rotationVector = this.getRotationVector();
        Vec3 bounce = getDeltaMovement().scale(-1);

        // The multiblock isn't finished placing itself until the block entities have been through a tick, so give it one
        // before handing the blocks to sable.
        LateTickOperation.schedule(serverLevel, 1, (level) ->
                LemonUtils.assembleIntoSubLevel(level, multiBlock, pos, shape, item.builtInRegistryHolder().getRegisteredName(), rotationVector, bounce));
    }

    public record DestroyEffectivity(double wood, double stone, double glass, double hay, boolean canDestroy) {
        public double getForBlock(Item item, BlockState block){
            Double fromItem = getFromItem(item, block);
            if (fromItem != null) return fromItem;

            if (block.is(ModBlockTags.LEMON_BIRDS_STONE)){
                return stone;
            } else if (block.is(ModBlockTags.LEMON_BIRDS_WOOD)){
                return wood;
            } else if (block.is(ModBlockTags.LEMON_BIRDS_GLASS)){
                return glass;
            } else if (block.is(ModBlockTags.LEMON_BIRDS_HAY)){
                return hay;
            }
            return 0;
        }

        private static @Nullable Double getFromItem(Item item, BlockState block) {
            Map<Either<TagKey<Block>, Block>, Double> map = item.builtInRegistryHolder().getData(ModDataMaps.BIRD_DESTROY_DATA);
            if (map != null) {
                Optional<Either<TagKey<Block>, Block>> any = map.keySet().stream().filter(either -> {
                    if (either.left().isPresent()) {
                        return block.is(either.left().get());
                    } else if (either.right().isPresent()){
                        return block.is(either.right().get());
                    }
                    return false;
                }).findAny();

                if (any.isPresent()) {
                    return map.get(any.get());
                }
            }

            if (block.is(ModBlockTags.BAD_PIGS)){
                return 0.75;
            }
            return null;
        }

        public DestroyEffectivity(double wood, double stone, double glass, double hay) {
            this(wood, stone, glass, hay, true);
        }

        public Vec3 applyMovementPostHit(Entity entity, Item item, BlockState state){
            return entity.getDeltaMovement().scale(getForBlock(item, state));
        }
    }

    public static final UnboundedMapCodec<Either<TagKey<Block>, Block>, Double> DESTROY_EFFECTIVITY_CODEC =
            Codec.unboundedMap(Codec.either(TagKey.hashedCodec(Registries.BLOCK), BuiltInRegistries.BLOCK.byNameCodec()), Codec.DOUBLE);
}
