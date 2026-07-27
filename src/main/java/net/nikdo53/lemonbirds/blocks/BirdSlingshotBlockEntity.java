package net.nikdo53.lemonbirds.blocks;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.Input;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
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
import net.nikdo53.lemonbirds.network.SlingshotDummyPosPayload;
import net.nikdo53.tinymultiblocklib.blockentities.AbstractMultiBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.UUID;

public class BirdSlingshotBlockEntity extends AbstractMultiBlockEntity {
    public BirdItem birdItem = null;
    public Vec3 lookVec = new Vec3(0, 0, 0);
    public Vec3 lookVecOld = new Vec3(0, 0, 0);

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
        dummyEntity.moveTo(getCenterPosition(this.getBlockPos()));
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

    public Vec3 getCenterPosition(@Nullable BlockPos pos){
        Vec3i normal = getDirection().getNormal().multiply(-1);
        Vec3 posVec = pos == null ? new Vec3(0.5, 0.5, 0.5) : pos.getCenter();
        return posVec.add(0,5.5,0).add(normal.getX(), normal.getY(), normal.getZ());
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

        lookVec = new Vec3(0, 0, 0);
        lookVecOld = new Vec3(0, 0, 0);
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
            bird.setControllingPlayer(player);
        }
    }

    public void updateDummyPos(Vec3 lookVec){
        this.lookVecOld = this.lookVec;
        this.lookVec = lookVec;

        Vec3 relativeDummyPos = getRelativeDummyPos(1);
       if (dummyEntity != null) {
           Vec3 pos = getCenterPosition(this.getBlockPos()).add(relativeDummyPos);
           dummyEntity.moveTo(pos.x(), pos.y(), pos.z());
       }
    }

    private @NotNull Direction getDirection() {
        return getBlockState().getValue(BirdSlingshotBlock.FACING);
    }

    public Vec3 getRelativeDummyPos(float partialTick){
        Direction direction = getDirection();
        Vec3 lookVec = getLookVec(partialTick);

        return switch (direction) {
            case NORTH -> new Vec3(-lookVec.x, lookVec.y, lookVec.z);
            case SOUTH -> new Vec3(lookVec.x, lookVec.y, -lookVec.z);
            case EAST -> new Vec3(-lookVec.z, lookVec.y, -lookVec.x);
           case WEST -> new Vec3(lookVec.z, lookVec.y, lookVec.x);
           default -> throw new IllegalArgumentException("Invalid direction: " + direction);
       };
    }

    public Vec3 getLookVec(float partialTick){
        double x = Mth.lerp(partialTick, lookVecOld.x, lookVec.x);
        double y = Mth.lerp(partialTick, lookVecOld.y, lookVec.y);
        double z = Mth.lerp(partialTick, lookVecOld.z, lookVec.z);
        return new Vec3(x, y, z);
    }

    public void shoot(@NotNull Projectile projectile){
        Vector3f pos = getRelativeDummyPos(1).toVector3f().mul(-1);

        float velocity = hasBirdItem() ? birdItem.getFlyingSpeed() : 1.0F;
        projectile.shoot(pos.x, pos.y, pos.z, velocity * 2, 0.1f);
        projectile.moveTo(getCenterPosition(this.getBlockPos()).add(getRelativeDummyPos(1)));
        getLevel().addFreshEntity(projectile);
    }


    public static class ClientThingy{
        public static final float MAX_DISTANCE = 15;

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
            double x = blockEntity.lookVec.x;
            float sensitivity = 0.1f;
            if (input.left != input.right){
                if (input.left){
                    x+= sensitivity;
                } else {
                    x+= -sensitivity;
                }
            }


            double y = blockEntity.lookVec.y;
            if (input.jumping != input.shiftKeyDown){
                if (input.jumping){
                    y+= sensitivity;
                } else {
                    y+= -sensitivity;
                }
            }


            double z = blockEntity.lookVec.z;
            if (input.forwardImpulse != 0){
                z -= input.forwardImpulse * sensitivity;
            }

            if (z < 0) return;

            if (isMaxDistance(x, y, z)){
                boolean isMaxDistance = true;
                for (int i = 0; i < 3; i++) {
                    z -= sensitivity;
                    if (!isMaxDistance(x, y, z)) {
                        isMaxDistance = false;
                        break;
                    };
                }
                if (isMaxDistance)
                    return;
            }

            blockEntity.updateDummyPos(new Vec3(x, y, z));
            PacketDistributor.sendToServer(new SlingshotDummyPosPayload(x, y, z));
        }

        private static boolean isMaxDistance(double x, double y, double z) {
            return new Vec3(x, y * 1.5, z).lengthSqr() > MAX_DISTANCE;
        }
    }

}
