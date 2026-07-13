package net.nikdo53.lemonbirds.entities;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.nikdo53.lemonbirds.init.ModBlockTags;
import net.nikdo53.lemonbirds.init.ModDataAttachments;
import net.nikdo53.lemonbirds.init.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractLemonBirdEntity extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Boolean> DATA_HAS_ABILITY = SynchedEntityData.defineId(
            AbstractLemonBirdEntity.class, EntityDataSerializers.BOOLEAN
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

    public void onAbilityKey(){
        Boolean hasAbility = entityData.get(DATA_HAS_ABILITY);
        if (hasAbility) {
            activateAbility();
            entityData.set(DATA_HAS_ABILITY, false);
        }
    }

    protected abstract void activateAbility();

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HAS_ABILITY, true);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        if (level().isClientSide){
            System.out.println("client!");
        }

        Level level = level();
        BlockPos pos = result.getBlockPos().immutable();
        LevelChunk chunk = getSubLevelChunk(level, pos);

        Vec3i normal = result.getDirection().getOpposite().getNormal();

        Vec3 movement = getDestroyEffectivity().applyMovementPostHit(this, chunk.getBlockState(pos));
        double speed = 10.0 * movement.lengthSqr();
        double size = 0.1 * speed;

        if (speed > 5) {
            BlockPos.betweenClosedStream(AABB.ofSize(getCoolPosition(pos), size, size, size))
                    .forEach(blockPos -> destroyBlock(blockPos, chunk));

        } else {
            if (!(level instanceof ServerLevel serverLevel)) return;

            ServerSubLevel subLevel = createOrGetSubLevel(serverLevel, pos);
            applyPhysics(serverLevel, subLevel, normal, speed * 5);

            discard();
        }
    }

    public static LevelChunk getSubLevelChunk(Level level, BlockPos pos) {
        SubLevel subLevel = (SubLevel) SableCompanion.INSTANCE.getContaining(level, pos);

        ChunkPos global = new ChunkPos(pos);
        if (subLevel != null) {
            LevelPlot plot = subLevel.getPlot();
            return plot.getChunk(plot.toLocal(global));
        }

        return level.getChunk(global.x, global.z);
    }

    public void destroyBlock(BlockPos pos, LevelChunk chunk) {
        BlockState state = chunk.getBlockState(pos);
        if (state.isAir()) return;

        chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);

    }

    public Vec3 getCoolPosition(BlockPos subLevelPos){
        Vec3 position = this.position();

        return new Vec3(
                subLevelPos.getX() + Mth.frac(position.x),
                subLevelPos.getY() + Mth.frac(position.y),
                subLevelPos.getZ() + Mth.frac(position.z)
        );

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

    private static void applyPhysics(ServerLevel level, ServerSubLevel subLevel, Vec3i normal, double scale) {
        SubLevelPhysicsSystem system = SubLevelPhysicsSystem.get(level);

        if (system != null) {
            Vec3 movement = new Vec3(normal.getX(), normal.getY(), normal.getZ()).scale(scale);
            movement = subLevel.logicalPose().transformNormalInverse(movement);

            RigidBodyHandle handle = RigidBodyHandle.of(subLevel);
            handle.applyLinearImpulse(JOMLConversion.toJOML(movement));

            subLevel.applyQueuedForces(system, handle, 5);
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (getOwner() instanceof Player player) {
            if (player.getData(ModDataAttachments.LEMON_BIRD) == this.getId()) {
                player.removeData(ModDataAttachments.LEMON_BIRD);
            }
        }

        super.remove(reason);
    }

    public record DestroyEffectivity(double wood, double stone, double glass, double hay) {
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

        public Vec3 applyMovementPostHit(Entity entity, BlockState state){
            Vec3 scaled = entity.getDeltaMovement().scale(getForBlock(state));
            entity.setDeltaMovement(scaled);
            return scaled;
        }
    }
}
