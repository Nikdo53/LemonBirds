package net.nikdo53.lemonbirds.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.nikdo53.lemonbirds.blocks.BirdSlingshotBlockEntity;
import net.nikdo53.lemonbirds.init.ModEntities;

public class DummyEntity extends Entity {
    private static final EntityDataAccessor<BlockPos> DATA_BE_POS = SynchedEntityData.defineId(
            DummyEntity.class, EntityDataSerializers.BLOCK_POS
    );
    public Player player;

    public DummyEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public DummyEntity(Level level, BlockPos pos, Player player) {
        super(ModEntities.DUMMY.get(), level);
        this.player = player;

        this.setBlockEntityPos(pos);
        this.setNoGravity(true);
        this.setInvulnerable(true);
    }

    /**
     * The pouch position is worked out from the slingshot on whichever side is asking, so the server's copy of it
     * arriving a round trip late has nothing to add - and applying it would drag the camera back off the position
     * the client already has it interpolating towards.
     */
    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
    }

    @Override
    public boolean shouldBeSaved() {
        // Only the slingshot knows this exists, and it does not remember it across a save, so a dummy that came back
        // with the chunk would be one nothing could ever get rid of.
        return false;
    }

    public BlockPos getBlockEntityPos() {
        return this.getEntityData().get(DATA_BE_POS);
    }

    public void setBlockEntityPos(BlockPos pos) {
        this.getEntityData().set(DATA_BE_POS, pos);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_BE_POS, BlockPos.ZERO);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    public void tick() {
        super.tick();

        // A dummy the slingshot has let go of - because the block entity went away, or reloaded and forgot it - has
        // nothing left pointing at it, so it would sit and tick for the rest of the world's life.
        if (tickCount % 20 == 0 && !level().isClientSide()){
            if (!(level().getBlockEntity(getBlockEntityPos()) instanceof BirdSlingshotBlockEntity blockEntity)
                    || blockEntity.dummyEntity != this){
                this.discard();
            }
        }
    }

    @Override
    public float getViewXRot(float partialTicks) {
        if (player == null) return 0;
        return player.getViewXRot(partialTicks);
    }

    @Override
    public float getViewYRot(float partialTick) {
        if (player == null) return 0;
        return player.getViewYRot(partialTick);
    }

    public Player getPlayerOrDiscard(){
        if (player == null) {
            if (level().getBlockEntity(getBlockEntityPos()) instanceof BirdSlingshotBlockEntity blockEntity) {
                blockEntity.endControl(null);
            } else {
                this.discard();
            }
            return null;
        }
        return player;
    }
}
