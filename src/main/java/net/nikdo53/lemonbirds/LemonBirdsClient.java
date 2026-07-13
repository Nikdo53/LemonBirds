package net.nikdo53.lemonbirds;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.nikdo53.lemonbirds.client.renderer.LemonBirdRenderer;
import net.nikdo53.lemonbirds.init.ModEntities;
import net.nikdo53.lemonbirds.init.ModItems;


@Mod(value = LemonBirds.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = LemonBirds.MOD_ID, value = Dist.CLIENT)

public class LemonBirdsClient {
    public LemonBirdsClient(ModContainer container) {

    }

    @SubscribeEvent
    public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.RED_LEMON_BIRD.get(), LemonBirdRenderer::new);
        event.registerEntityRenderer(ModEntities.BOMB_LEMON_BIRD.get(), LemonBirdRenderer::new);
        event.registerEntityRenderer(ModEntities.YELLOW_LEMON_BIRD.get(), LemonBirdRenderer::new);
        event.registerEntityRenderer(ModEntities.BLUE_LEMON_BIRD.get(), LemonBirdRenderer::new);
    }

}
