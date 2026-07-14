package net.nikdo53.lemonbirds.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.entities.AbstractLemonBirdEntity;
import net.nikdo53.lemonbirds.init.ModDataAttachments;
import net.nikdo53.lemonbirds.network.ActivateLemonBirdPayload;

@EventBusSubscriber(modid = LemonBirds.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {


    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        if (event.getKey() == InputConstants.KEY_K) {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            ClientLevel level = minecraft.level;

            if (player == null || level == null) return;

            Integer entityId = player.getData(ModDataAttachments.LEMON_BIRD);
            if (entityId == -1) return;

            Entity entity = level.getEntity(entityId);
            if (!(entity instanceof AbstractLemonBirdEntity lemonBird))
                throw new IllegalStateException("Entity with ID " + entityId + " is not a Lemon Bird! WTF");

            PacketDistributor.sendToServer(new ActivateLemonBirdPayload(entityId));
            lemonBird.onAbilityKey();
        }
    }

}
