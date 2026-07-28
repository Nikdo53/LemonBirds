package net.nikdo53.lemonbirds.blocks;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.Input;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.nikdo53.lemonbirds.entities.AbstractLemonBirdEntity;
import net.nikdo53.lemonbirds.entities.BombLemonBirdEntity;
import net.nikdo53.lemonbirds.entities.DummyEntity;
import net.nikdo53.lemonbirds.entities.DummyProjectile;
import net.nikdo53.lemonbirds.init.ModBlockEntities;
import net.nikdo53.lemonbirds.init.ModBlocks;
import net.nikdo53.lemonbirds.init.ModDataAttachments;
import net.nikdo53.lemonbirds.init.ModKeyBinds;
import net.nikdo53.lemonbirds.items.BirdItem;
import net.nikdo53.lemonbirds.network.SlingshotRotationPayload;
import net.nikdo53.tinymultiblocklib.blockentities.AbstractMultiBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BirdSlingshotBlockEntity extends AbstractMultiBlockEntity {
    // Measurements taken from BirdSlingshotModel, so that the block entity and the renderer agree on where the sling is.
    /** Height of the fork tips - where the bands are anchored - above the bottom of the center block. */
    public static final double ANCHOR_HEIGHT = 1.5 + 70.2843 / 16.0;
    /** How far behind the fork tips the bands leave the fork. */
    public static final double BAND_START = 8.0 / 16.0;
    /** Length of the bands as they are modelled, meaning with nothing pulling on them. */
    public static final double BAND_REST_LENGTH = 51.0 / 16.0;
    /** Distance from the fork tips to the pouch while the bands are relaxed. */
    public static final double POUCH_REST_DISTANCE = BAND_START + BAND_REST_LENGTH;

    public static final float MAX_PULL = 3.8F;
    public static final float MAX_YAW = 30;
    public static final float MAX_PITCH = 30;

    public BirdItem birdItem = null;

    /** Aim rotation, in degrees, relative to the way the slingshot is facing. Positive yaw aims left, positive pitch aims up. */
    public float yaw = 0;
    public float pitch = 0;
    /** How far the pouch is pulled back, in blocks. */
    public float pull = 0;

    public float yawOld = 0;
    public float pitchOld = 0;
    public float pullOld = 0;

    public DummyEntity dummyEntity = null;
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

    public void beginControl(Player player) {
        assert level != null;
        controllingPlayer = player;

        dummyEntity = new DummyEntity(level, this.getBlockPos(), player);
        moveDummyToPouch();
        level.addFreshEntity(dummyEntity);

        player.setData(ModDataAttachments.SLINGSHOT, this.getBlockPos());

        if (level.isClientSide()){
            ClientThingy.beginControlClient(player, this);
        }

    }

    public void tick(Level level, BlockPos pos, BlockState state){
         if (isBeingControlled() && level.getGameTime() % 40 == 0){
             DummyProjectile projectile = new DummyProjectile(level);
             shoot(projectile);
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
        dummyEntity.discard();
        dummyEntity = null;

        yaw = yawOld = 0;
        pitch = pitchOld = 0;
        pull = pullOld = 0;
    }

    public void onKeyPressed(Player player, int key){
        if (key == InputConstants.KEY_E || key == InputConstants.KEY_ESCAPE){
            endControl(player);
        }

        if (key == ModKeyBinds.SLINGSHOT_LAUNCH.getKey().getValue() && birdItem != null){
            AbstractLemonBirdEntity bird = birdItem.useFunction.apply(level, player);
            bird.setOwner(player);
            bird.setItem(birdItem.getDefaultInstance());

            shoot(bird);
            endControl(player);
            birdItem = null;
            bird.setControllingPlayer(player);
        }
    }

    public void updateRotation(float yaw, float pitch, float pull){
        this.yawOld = this.yaw;
        this.pitchOld = this.pitch;
        this.pullOld = this.pull;

        this.yaw = Mth.clamp(yaw, -MAX_YAW, MAX_YAW);
        this.pitch = Mth.clamp(pitch, -MAX_PITCH, MAX_PITCH);
        this.pull = Mth.clamp(pull, 0, MAX_PULL);

        moveDummyToPouch();
    }

    /** Keeps the camera sat in the pouch, so that what the player sees lines up with what the renderer draws. */
    private void moveDummyToPouch(){
        if (dummyEntity == null) return;

        Vec3 pos = getPouchPosition(1).subtract(0, dummyEntity.getEyeHeight(), 0);
        dummyEntity.moveTo(pos.x(), pos.y(), pos.z());
    }

    private @NotNull Direction getDirection() {
        return getBlockState().getValue(BirdSlingshotBlock.FACING);
    }

    /** The fork tips, which the bands hang from and the pouch swings around. */
    public Vec3 getAnchorPosition(BlockPos pos){
        return new Vec3(pos.getX() + 0.5, pos.getY() + ANCHOR_HEIGHT, pos.getZ() + 0.5);
    }

    /** Where the pouch - and with it the bird and the camera - has ended up. */
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
        projectile.shoot(direction.x(), direction.y(), direction.z(), velocity * 2, 0.1f);
        projectile.moveTo(getPouchPosition(1));
        getLevel().addFreshEntity(projectile);
    }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (birdItem != null) tag.putString("birdItem", BuiltInRegistries.ITEM.getKey(birdItem).toString());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        String string = tag.getString("birdItem");
        if (!string.isBlank()){
            birdItem = (BirdItem) BuiltInRegistries.ITEM.get(ResourceLocation.parse(string));
        }
    }

    public static class ClientThingy{
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
            minecraft.setCameraEntity(blockEntity.dummyEntity);
        }

        public static void onInput(BirdSlingshotBlockEntity blockEntity, Input input){
            float rotationSensitivity = 2.0f;
            float pullSensitivity = 0.1f;

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

            blockEntity.updateRotation(yaw, pitch, pull);
            PacketDistributor.sendToServer(new SlingshotRotationPayload(yaw, pitch, pull));
        }
    }

}
