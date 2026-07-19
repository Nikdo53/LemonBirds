package net.nikdo53.lemonbirds.entities;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.*;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.blocks.BirdSlingshotBlockEntity;
import net.nikdo53.lemonbirds.blocks.FallingBirdBlock;
import net.nikdo53.lemonbirds.init.*;
import net.nikdo53.lemonbirds.items.BirdItem;
import net.nikdo53.lemonbirds.util.LateTickOperation;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractLemonBirdEntity extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Boolean> DATA_HAS_ABILITY = SynchedEntityData.defineId(
            AbstractLemonBirdEntity.class, EntityDataSerializers.BOOLEAN
    );

    private static final EntityDataAccessor<Integer> HIT_COOLDOWN = SynchedEntityData.defineId(
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
        return ModItems.RED_BIRD.asItem();
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, inaccuracy);
        Entity owner = getOwner();
        if (owner instanceof Player player) {
            player.setData(ModDataAttachments.LEMON_BIRD, getId());
        }
    }

    public void onAbilityKey(){
        if (hasAbility()) {
            entityData.set(DATA_HAS_ABILITY, false);
            activateAbility();
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

    protected abstract void activateAbility();

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
        builder.define(HIT_COOLDOWN, 0);
        builder.define(CONTROLLING_PLAYER_ID, Optional.empty());
    }


    @Override
    public void tick() {
        super.tick();
        if (entityData.get(HIT_COOLDOWN) > 0) {
            entityData.set(HIT_COOLDOWN, entityData.get(HIT_COOLDOWN) - 1);
        }

        if (level().isClientSide()){
            level().addParticle(ModParticles.LEMON_BIRD_TRAIL.get(), getX(), getY() + 0.5, getZ(), 0, 0, 0);
        }

        if (getOwner() instanceof Player player) {
            setRot(player.getYRot(), player.getXRot());
            this.yRotO = player.getYRot();
            this.xRotO = player.getXRot();
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
        if (entityData.get(HIT_COOLDOWN) > 0) {
            return;
        }
        super.onHitBlock(result);

        Level level = level();
        BlockPos pos = result.getBlockPos().immutable();
        if (level.getBlockState(pos).is(ModBlocks.SLING_SHOT))
            return;

        Vec3 location = result.getLocation();
        Vec3 normal = location.subtract(pos.getX(), pos.getY(), pos.getZ()).subtract(0.5, 0.5, 0.5).multiply(-1, -1, -1);

        Vec3 movement = getDestroyEffectivity().applyMovementPostHit(this, level.getBlockState(pos));
        double speed = 10.0 * movement.lengthSqr();
        double size = 0.1 * speed;

        if (speed > 5 && getDestroyEffectivity().canDestroy) {
            BlockPos.betweenClosedStream(AABB.ofSize(location, size, size, size))
                    .forEach(blockPos -> level.destroyBlock(blockPos, false));

            entityData.set(HIT_COOLDOWN, 1);

        } else {
            if (level instanceof ServerLevel serverLevel) {
                ServerSubLevel subLevel = (ServerSubLevel) SableCompanion.INSTANCE.getContaining(level, pos);
                applyPhysics(serverLevel, subLevel, normal, speed * 12);
            }

            discard();
        }
    }

    private static @Nullable ServerSubLevel createOrGetSubLevel(ServerLevel level, BlockPos pos) {
        ServerSubLevel subLevel = (ServerSubLevel) SableCompanion.INSTANCE.getContaining(level, pos);

        if (subLevel == null) {


            int size = 2;
            final BoundingBox boundingBox = BoundingBox.fromCorners(pos.offset(size, size, size), pos.offset(-size, -size, -size));

            final List<BlockPos> blocks = BlockPos.betweenClosedStream(boundingBox).map(BlockPos::immutable).toList();
            final BlockPos anchor = blocks.getFirst();

            final BoundingBox3i bounds = new BoundingBox3i(boundingBox);
            bounds.set(
                    bounds.minX - 1,
                    bounds.minY - 1,
                    bounds.minZ - 1,
                    bounds.maxX + 1,
                    bounds.maxY + 1,
                    bounds.maxZ + 1
            );

            subLevel = SubLevelAssemblyHelper.assembleBlocks(level, anchor, blocks, bounds);

        }

        return subLevel;
    }

    private static void applyPhysics(ServerLevel level, ServerSubLevel subLevel, Vec3 normal, double scale) {
        SubLevelPhysicsSystem system = SubLevelPhysicsSystem.get(level);

        if (system != null && subLevel != null) {
            Vec3 movement = normal.scale(scale);
           // movement = subLevel.logicalPose().transformNormalInverse(movement);

            RigidBodyHandle handle = RigidBodyHandle.of(subLevel);
            handle.applyLinearImpulse(JOMLConversion.toJOML(movement));
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (getOwner() instanceof Player player) {
            if (player.getData(ModDataAttachments.LEMON_BIRD) == this.getId()) {
                player.removeData(ModDataAttachments.LEMON_BIRD);
            }
        }

        if (getControllingPlayer().isPresent()){
            BirdSlingshotBlockEntity.ClientThingy.resetCamera(getControllingPlayer().get());
        }
        turnIntoBlock();

        super.remove(reason);
    }

    @Override
    public float getViewXRot(float partialTicks) {
        if (getOwner() != null){
            return getOwner().getViewXRot(partialTicks);
        }
        return super.getViewXRot(partialTicks);
    }

    @Override
    public float getViewYRot(float partialTick) {
        if (getOwner() != null){
            return getOwner().getViewYRot(partialTick);
        }
        return super.getViewYRot(partialTick);
    }

    public void turnIntoBlock(){
        Item item = getItem().getItem();
        if (item instanceof BirdItem birdItem) {
            Optional<Block> block = birdItem.getBlock();
            if (block.isEmpty()) return;

            BlockState state = block.get().defaultBlockState();
            state = state.setValue(FallingBirdBlock.DESPAWNS, true);

            BlockPos pos = getOnPos();
            level().setBlock(pos, state, 3);
            level().blockEntityChanged(pos);

            if (level() instanceof ServerLevel serverLevel) {
                final BoundingBox3i bounds = new BoundingBox3i(pos, pos);
                bounds.set(
                        bounds.minX - 1,
                        bounds.minY - 1,
                        bounds.minZ - 1,
                        bounds.maxX + 1,
                        bounds.maxY + 1,
                        bounds.maxZ + 1
                );

                ServerSubLevel subLevel;
                try {
                    subLevel = SubLevelAssemblyHelper.assembleBlocks(serverLevel, pos, List.of(pos), bounds);
                } catch (ArrayIndexOutOfBoundsException e){
                    LemonBirds.LOGGER.error("Unable to create sub-level cuz sable sucks");
                    return;
                }

                Vec2 rotationVector = this.getRotationVector();
                Vec3 deltaMovement = getDeltaMovement().scale(-1);
                if (subLevel != null) {
                    SubLevelPhysicsSystem system = SubLevelPhysicsSystem.get(serverLevel);
                    Pose3d pose = subLevel.logicalPose();

                    Quaterniond orientation = new Quaterniond();

                    orientation.rotateY(-Math.toRadians(rotationVector.y));
                    orientation.rotateX(Math.toRadians(rotationVector.x));

                    pose.orientation().set(orientation);
                    system.getPipeline().teleport(subLevel, pose.position(), pose.orientation());

                }

                LateTickOperation.SUB_LEVEL_OPERATIONS.add(new LateTickOperation(5, (lvl) -> {
                    if (subLevel != null) {
                        applyPhysics(lvl, subLevel, deltaMovement, 10);
                    }
                }));

            }

        }
    }

    public record DestroyEffectivity(double wood, double stone, double glass, double hay, boolean canDestroy) {
        public double getForBlock(BlockState block){
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

        public DestroyEffectivity(double wood, double stone, double glass, double hay) {
            this(wood, stone, glass, hay, true);
        }

        public Vec3 applyMovementPostHit(Entity entity, BlockState state){
            Vec3 scaled = entity.getDeltaMovement().scale(getForBlock(state));
            entity.setDeltaMovement(scaled);
            return scaled;
        }
    }
}
