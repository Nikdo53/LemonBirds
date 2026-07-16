package net.nikdo53.lemonbirds.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.entities.AbstractLemonBirdEntity;
import net.nikdo53.lemonbirds.init.ModDataAttachments;
import net.nikdo53.lemonbirds.network.ActivateLemonBirdPayload;
import org.joml.Vector3f;

@EventBusSubscriber(modid = LemonBirds.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {


    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        if (event.getKey() == InputConstants.KEY_K) {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            ClientLevel level = minecraft.level;

            if (player == null || level == null) return;

            int entityId = player.getData(ModDataAttachments.LEMON_BIRD);
            if (entityId == -1) return;

            Entity entity = level.getEntity(entityId);
            if (!(entity instanceof AbstractLemonBirdEntity lemonBird))
                throw new IllegalStateException("Entity with ID " + entityId + " is not a Lemon Bird! WTF");

            Vec3 position = entity.position();

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


            PacketDistributor.sendToServer(new ActivateLemonBirdPayload(entityId));
            lemonBird.onAbilityKey();
        }
    }

}
