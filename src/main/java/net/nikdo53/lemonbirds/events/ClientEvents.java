package net.nikdo53.lemonbirds.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CalculateDetachedCameraDistanceEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.blocks.BirdSlingshotBlockEntity;
import net.nikdo53.lemonbirds.entities.AbstractLemonBirdEntity;
import net.nikdo53.lemonbirds.init.ModDataAttachments;
import net.nikdo53.lemonbirds.init.ModKeyBinds;
import net.nikdo53.lemonbirds.network.ActivateLemonBirdPayload;
import net.nikdo53.lemonbirds.network.SlingshotKeyPressPayload;
import org.joml.Vector3f;

@EventBusSubscriber(modid = LemonBirds.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {


    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;

        if (player == null || level == null) return;
        int key = event.getKey();
        onBirdKey(key);


        BlockPos pos = player.getExistingDataOrNull(ModDataAttachments.SLINGSHOT);
        if (pos != null) {
            if (level.getBlockEntity(pos) instanceof BirdSlingshotBlockEntity blockEntity) {
                if (blockEntity.controllingPlayer != player) return;
                BirdSlingshotBlockEntity.Action action = BirdSlingshotBlockEntity.ClientThingy.actionFromKey(key);
                if (action != null) {
                    blockEntity.onKeyPressed(player, action);
                    PacketDistributor.sendToServer(new SlingshotKeyPressPayload(action));
                }
            } else {
                minecraft.setCameraEntity(player);
                player.removeData(ModDataAttachments.SLINGSHOT);
            }


        }
    }

    public static void onBirdKey(int key){
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;

        int entityId = player.getData(ModDataAttachments.LEMON_BIRD);
        if (entityId == -1) return;

        // The id outlives the bird by a tick or two whenever it dies, so a miss here is normal.
        if (!(level.getEntity(entityId) instanceof AbstractLemonBirdEntity lemonBird)) return;

        Vec3 position = lemonBird.position();

        if (key == ModKeyBinds.BIRD_ABILITY.getKey().getValue()) {
            if (lemonBird.hasAbility()) {
                int gridSize = 2;
                for (int x = -gridSize; x < gridSize; x++) {
                    for (int y = -gridSize; y < gridSize; y++) {
                        for (int z = -gridSize; z < gridSize; z++) {

                            level.addParticle(new DustParticleOptions(new Vector3f(1, 1, 1), 1),
                                    position.x() + (x / 2f),
                                    position.y() + (y / 2f),
                                    position.z() + (z / 2f),
                                    0, 0, 0);
                        }
                    }
                }

            }

            Vec2 rotation = getCameraRotation(minecraft);
            PacketDistributor.sendToServer(new ActivateLemonBirdPayload(entityId, rotation.x, rotation.y));
            lemonBird.onAbilityKey(rotation.x, rotation.y);
        }

        if (key == InputConstants.KEY_E || key == InputConstants.KEY_ESCAPE){
            lemonBird.setControllingPlayer(null);
            minecraft.setCameraEntity(player);
        }

    }

    /**
     * The rotation the camera is currently looking along, as {@code (xRot, yRot)}.
     * <p>
     * Camera#setup takes the camera entity's {@code getViewXRot}/{@code getViewYRot}, not its {@code xRot}/{@code
     * yRot} fields, so those are what an ability has to be aimed with. The fields only happen to agree while the
     * player is the camera entity; once something else is - a bird launched from a slingshot, say - they are a tick
     * behind and network synced, and the ability ends up aimed somewhere the player never looked.
     */
    private static Vec2 getCameraRotation(Minecraft minecraft) {
        Entity cameraEntity = minecraft.getCameraEntity() != null ? minecraft.getCameraEntity() : minecraft.player;
        float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(false);

        return new Vec2(cameraEntity.getViewXRot(partialTick), cameraEntity.getViewYRot(partialTick));
    }

    @SubscribeEvent
    public static void cameraDistance(CalculateDetachedCameraDistanceEvent event){
        if (event.getCamera().getEntity() instanceof AbstractLemonBirdEntity lemonBird) {
            event.setDistance(event.getDistance() * 2);
        }
    }

    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;
        Input input = event.getInput();


        BlockPos pos = player.getExistingDataOrNull(ModDataAttachments.SLINGSHOT);
        if (pos != null) {
            if (level.getBlockEntity(pos) instanceof BirdSlingshotBlockEntity blockEntity) {
                if (blockEntity.controllingPlayer != player) return;

                BirdSlingshotBlockEntity.ClientThingy.onInput(blockEntity, input);
            }
        }

    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity cameraEntity = minecraft.getCameraEntity();
        if (cameraEntity != null && cameraEntity.isRemoved()){
            minecraft.setCameraEntity(minecraft.player);
        }
    }

}