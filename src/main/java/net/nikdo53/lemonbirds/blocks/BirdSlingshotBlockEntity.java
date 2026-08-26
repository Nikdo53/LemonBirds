package net.nikdo53.lemonbirds.blocks;

import com.mojang.blaze3d.platform.InputConstants;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.Input;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.nikdo53.lemonbirds.entities.AbstractLemonBirdEntity;
import net.nikdo53.lemonbirds.entities.BombLemonBirdEntity;
import net.nikdo53.lemonbirds.entities.DummyEntity;
import net.nikdo53.lemonbirds.entities.DummyProjectile;
import net.nikdo53.lemonbirds.init.*;
import net.nikdo53.lemonbirds.items.BirdItem;
import net.nikdo53.lemonbirds.network.SlingshotRotationPayload;
import net.nikdo53.lemonbirds.network.SlingshotRotationSyncPayload;
import net.nikdo53.lemonbirds.util.StringRepresentableAutoForEnums;
import net.nikdo53.tinymultiblocklib.blockentities.AbstractMultiBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.IntFunction;

public class BirdSlingshotBlockEntity extends AbstractMultiBlockEntity {
    public static final double ANCHOR_HEIGHT = 1.5 + 70.2843 / 16.0;
    public static final double BAND_START = 8.0 / 16.0;
    public static final double BAND_REST_LENGTH = 51.0 / 16.0;
    public static final double POUCH_REST_DISTANCE = BAND_START + BAND_REST_LENGTH;

    public static final float MAX_YAW = 720;
    public static final float MAX_PULL = 4.0F;
    public static final float MAX_PITCH = 30;

    /** What the aim keys are worth while the use key is held, for lining a shot up rather than swinging it around. */
    public static final float PRECISION_AIM_FACTOR = 0.1F;

    public BirdItem birdItem = null;

    public float yaw = 0;
    public float pitch = 0;
    public float pull = 0;

    public float yawOld = 0;
    public float pitchOld = 0;
    public float pullOld = 0;

    /** Whether the aim moved since the last tick, so {@link #tick} knows whether the interpolation is still live. */
    private boolean aimMoved = false;

    public DummyEntity dummyEntity = null;
    public int entityId = -1;

    public Player controllingPlayer = null;


    public BirdSlingshotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SLING_SHOT.get(), pos, state);
    }

    public void setBirdItem(BirdItem birdItem){
        this.birdItem = birdItem;
    }

    public boolean hasBirdItem(){
        return birdItem != null;
    }

    public boolean isBeingControlled(){
        return dummyEntity != null;
    }

    /**
     * Whether the player is stood at a slingshot aiming it, on either side.
     * <p>
     * The attachment is set on the server when control begins and synced from there, so this answers the same on
     * both sides and the interaction handlers can be written once for the pair.
     */
    public static boolean isControllingSlingshot(Player player){
        return player.getExistingDataOrNull(ModDataAttachments.SLINGSHOT) != null;
    }

    public DummyEntity getDummyEntity(Level level){
        if (level.isClientSide() && level.getEntity(entityId) instanceof DummyEntity ret){
            return ret;
        }
        return dummyEntity;
    }

    public void beginControl(Player player) {
        assert level != null;
        if (level.isClientSide()) return;

        controllingPlayer = player;

        dummyEntity = new DummyEntity(level, this.getBlockPos(), player);
        // moveTo rather than the tick's setPos: the dummy is still sitting at the origin it was constructed at, and
        // that is not somewhere the camera should be seen interpolating out of on the first frame.
        dummyEntity.moveTo(getPouchPosition(1).subtract(0, dummyEntity.getEyeHeight(), 0));
        level.addFreshEntity(dummyEntity);

        player.setData(ModDataAttachments.SLINGSHOT, this.getBlockPos());
    }

    public void tick(Level level, BlockPos pos, BlockState state){
         // Every block of the multiblock carries one of these, and only the centre one is aimed, rendered or holds
         // the camera anchor. The other fourteen have nothing to do.
         if (!isCenter()) return;

         if (isBeingControlled() && level.getGameTime() % 40 == 0){
             DummyProjectile projectile = new DummyProjectile(level);
             shoot(projectile);
         }

         // Block entities tick after entities do, so the aim already holds whatever this tick's input or packet made
         // of it and the old values are the tick behind that the renderer interpolates from. Once nothing is moving
         // the two have to settle back together, or every frame would swing between them as the partial tick cycles.
         if (aimMoved) {
             aimMoved = false;
         } else {
             yawOld = yaw;
             pitchOld = pitch;
             pullOld = pull;
         }

         if (level.isClientSide()) {
             moveDummyToPouch();
         }
    }

    public void endControl(@Nullable Player player){
        if (player != null) {
            if (player.level().isClientSide()) {
                ClientThingy.resetCamera(player.getUUID());
            }

            player.removeData(ModDataAttachments.SLINGSHOT);
        }

        controllingPlayer = null;
        if (dummyEntity != null) {
            dummyEntity.discard();
        }
        entityId = -1;
        dummyEntity = null;

        yaw = 0;
        pitch = 0;
        pull = 0;

        yawOld = 0;
        pitchOld = 0;
        pullOld = 0;
    }

    public void onKeyPressed(Player player, Action action){
        if (action == Action.END_CONTROL){
            endControl(player);
        }

        if (action == Action.LAUNCH_BIRD && hasBirdItem()){
            // The bird is server authoritative. Launching one client side too would only build a bird that
            // ClientLevel silently drops on addFreshEntity - but not before Projectile#shoot has pointed the
            // player's LEMON_BIRD attachment at its id, which then resolves to nothing and leaves the ability
            // action with no bird to fire.
            if (level != null && !level.isClientSide()) {
                AbstractLemonBirdEntity bird = birdItem.useFunction.apply(level, player);
                bird.setOwner(player);
                bird.setItem(birdItem.getDefaultInstance());
                bird.setControllingPlayer(player);

                shoot(bird);
            }

            endControl(player);
            birdItem = null;
        }

        sync();
    }

    public enum Action implements StringRepresentableAutoForEnums {
        END_CONTROL,
        LAUNCH_BIRD;

        public static final IntFunction<Action> BY_ID = ByIdMap.continuous(Action::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, Action> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Action::ordinal);
    }

    /** @return whether the aim ended up anywhere new, so that a caller can skip telling anyone about a no-op. */
    public boolean updateRotation(float yaw, float pitch, float pull){
        float newYaw = Mth.clamp(yaw, -MAX_YAW, MAX_YAW);
        float newPitch = Mth.clamp(pitch, -MAX_PITCH, MAX_PITCH);
        float newPull = Mth.clamp(pull, 0, MAX_PULL);

        if (newYaw == this.yaw && newPitch == this.pitch && newPull == this.pull) return false;

        // Keep where the aim was for the renderer to interpolate out of. Only the first move of a tick may do this -
        // a second one would throw the tick's starting point away and flatten the interpolation into a step.
        if (!aimMoved) {
            this.yawOld = this.yaw;
            this.pitchOld = this.pitch;
            this.pullOld = this.pull;
            aimMoved = true;
        }

        this.yaw = newYaw;
        this.pitch = newPitch;
        this.pull = newPull;

        // Nothing else tells the other players the slingshot is being aimed - the block entity is only resynced when
        // a bird changes hands, and that is far too heavy to do every tick anyway.
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new ChunkPos(getBlockPos()),
                    new SlingshotRotationSyncPayload(getBlockPos(), this.yaw, this.pitch, this.pull));
        }

        return true;
    }

    public void sync(){
        this.setChanged();
        if (level instanceof ServerLevel serverLevel){
            serverLevel.getChunkSource().blockChanged(this.getBlockPos());
        }
    }

    /**
     * Keeps the camera anchor on the pouch.
     * <p>
     * Has to run from the block entity tick and nowhere else. Entities stash their previous position at the top of
     * their own tick, and block entities tick after all of them, so a position set here is one the camera can be
     * interpolated up to over the tick. Setting it from the input handler instead left the dummy's own tick to come
     * along afterwards and record the new position as the old one too, and the camera stepped at 20Hz.
     */
    private void moveDummyToPouch(){
        DummyEntity entity = getDummyEntity(getLevel());
        if (entity == null) return;

        Vec3 pos = getPouchPosition(1).subtract(0, entity.getEyeHeight(), 0);
        entity.setPos(pos.x(), pos.y(), pos.z());
    }

    private @NotNull Direction getDirection() {
        return getBlockState().getValue(BirdSlingshotBlock.FACING);
    }

    public Vec3 getAnchorPosition(BlockPos pos){
        return new Vec3(pos.getX() + 0.5, pos.getY() + ANCHOR_HEIGHT, pos.getZ() + 0.5);
    }

    public Vec3 getPouchPosition(float partialTick){
        return getAnchorPosition(this.getBlockPos()).add(getRelativePouchPos(partialTick));
    }

    public Vec3 getRelativePouchPos(float partialTick){
        return toWorldSpace(getAimVec(partialTick).scale(getPouchDistance(partialTick)));
    }

    /** How far the pouch hangs behind the fork tips: the slack in the bands plus whatever has been pulled on top of it. */
    public double getPouchDistance(float partialTick){
        return POUCH_REST_DISTANCE + getPull(partialTick);
    }

    /** Direction the bird gets launched in, opposite to the way the pouch is pulled. */
    public Vec3 getShootDirection(float partialTick){
        return toWorldSpace(getAimVec(partialTick)).scale(-1);
    }

    private Vec3 toWorldSpace(Vec3 local){
        Direction direction = getDirection();

        return switch (direction) {
            case NORTH -> new Vec3(-local.x, local.y, local.z);
            case SOUTH -> new Vec3(local.x, local.y, -local.z);
            case EAST -> new Vec3(-local.z, local.y, -local.x);
           case WEST -> new Vec3(local.z, local.y, local.x);
           default -> throw new IllegalArgumentException("Invalid direction: " + direction);
       };
    }

    /** Unit vector pointing backwards along the aim, in slingshot-local space. */
    public Vec3 getAimVec(float partialTick){
        double yawRad = Math.toRadians(getYaw(partialTick));
        double pitchRad = Math.toRadians(getPitch(partialTick));
        double horizontal = Math.cos(pitchRad);

        return new Vec3(
                Math.sin(yawRad) * horizontal,
                Math.sin(pitchRad),
                Math.cos(yawRad) * horizontal
        );
    }

    public float getYaw(float partialTick){
        return Mth.lerp(partialTick, yawOld, yaw);
    }

    public float getPitch(float partialTick){
        return Mth.lerp(partialTick, pitchOld, pitch);
    }

    public float getPull(float partialTick){
        return Mth.lerp(partialTick, pullOld, pull);
    }

    public void shoot(@NotNull Projectile projectile){
        Vec3 direction = getShootDirection(1);

        float velocity = hasBirdItem() ? birdItem.getFlyingSpeed() : 1.0F;
        projectile.shoot(direction.x(), direction.y(), direction.z(), (float) ((velocity * pull /3f + 0.5f) * ModServerConfig.SLINGSHOT_RANGE_MULTIPLIER.getAsDouble()), 0.1f);
        projectile.moveTo(getPouchPosition(1));
        getLevel().addFreshEntity(projectile);

        setChanged();
    }
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (birdItem != null) tag.putString("birdItem", BuiltInRegistries.ITEM.getKey(birdItem).toString());
        tag.putFloat("yaw", yaw);
        tag.putFloat("pitch", pitch);
        tag.putFloat("pull", pull);
        if (dummyEntity != null)
            tag.putInt("entityId", dummyEntity.getId());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        String string = tag.getString("birdItem");
        if (!string.isBlank()){
            birdItem = (BirdItem) BuiltInRegistries.ITEM.get(ResourceLocation.parse(string));
        } else {
            birdItem = null;
        }

        yawOld = yaw;
        pitchOld = pitch;
        pullOld = pull;

        yaw = tag.getFloat("yaw");
        pitch = tag.getFloat("pitch");
        pull = tag.getFloat("pull");
        if (tag.contains("entityId")) {
            entityId = tag.getInt("entityId");
        } else {
            entityId = -1;
        }
    }

    public static class ClientThingy{
        public static Action actionFromKey(int key){
            if (key == ModKeyBinds.END_CAMERA_CONTROL.getKey().getValue() || key == InputConstants.KEY_ESCAPE){
                return Action.END_CONTROL;
            } else if (key == ModKeyBinds.SLINGSHOT_LAUNCH.getKey().getValue()){
                return Action.LAUNCH_BIRD;
            } else {
                return null;
            }
        }
        public static void trySetCamera(AbstractLemonBirdEntity entity, UUID uuid){
            Minecraft minecraft = Minecraft.getInstance();
            if (!minecraft.player.getUUID().equals(uuid)) return;

            minecraft.setCameraEntity(entity);
            entity.setOwner(minecraft.player);
        }

        public static void resetCamera(UUID uuid){
            Minecraft minecraft = Minecraft.getInstance();
            if (!minecraft.player.getUUID().equals(uuid)) return;

            minecraft.setCameraEntity(minecraft.player);

        }

        public static void beginControlClient(Player player, BirdSlingshotBlockEntity blockEntity) {
            Minecraft minecraft = Minecraft.getInstance();
            Camera camera = minecraft.gameRenderer.getMainCamera();
            ClientLevel level = minecraft.level;

            assert level != null;
            minecraft.setCameraEntity(blockEntity.getDummyEntity(level));
        }

        public static void onInput(BirdSlingshotBlockEntity blockEntity, Input input){
            // The use key has nothing else to do here - a player on a slingshot cannot place or use anything - so it
            // is free to mean "carefully" instead.
            float precision = Minecraft.getInstance().options.keyUse.isDown() ? PRECISION_AIM_FACTOR : 1.0f;

            float rotationSensitivity = 2.0f * precision;
            float pullSensitivity = 0.1f * precision;

            float yaw = blockEntity.yaw;
            if (input.left != input.right){
                if (input.left){
                    yaw += rotationSensitivity;
                } else {
                    yaw -= rotationSensitivity;
                }
            }


            float pitch = blockEntity.pitch;
            if (input.jumping != input.shiftKeyDown){
                if (input.jumping){
                    pitch += rotationSensitivity;
                } else {
                    pitch -= rotationSensitivity;
                }
            }


            float pull = blockEntity.pull;
            if (input.forwardImpulse != 0){
                pull -= input.forwardImpulse * pullSensitivity;
            }

            // This runs every tick the player is on the slingshot, whether or not they are touching anything, so
            // only bother the server on the ticks the aim actually went somewhere.
            if (blockEntity.updateRotation(yaw, pitch, pull)) {
                PacketDistributor.sendToServer(new SlingshotRotationPayload(yaw, pitch, pull));
            }
        }
    }

}
