package net.nikdo53.lemonbirds;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.nikdo53.lemonbirds.client.model.BirdSlingshotModel;
import net.nikdo53.lemonbirds.client.renderer.*;
import net.nikdo53.lemonbirds.init.ModBlockEntities;
import net.nikdo53.lemonbirds.init.ModEntities;
import net.nikdo53.lemonbirds.init.ModKeyBinds;
import net.nikdo53.lemonbirds.init.ModParticles;
import net.nikdo53.lemonbirds.particle.BirdTrailParticle;


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
        event.registerEntityRenderer(ModEntities.MATILDA_LEMON_BIRD.get(), LemonBirdRenderer::new);
        event.registerEntityRenderer(ModEntities.TERENCE_LEMON_BIRD.get(), LemonBirdRenderer::new);

        event.registerEntityRenderer(ModEntities.RED_SCREAM_ENTITY.get(), RedScreamRenderer::new);
        event.registerEntityRenderer(ModEntities.MATILDA_EGG_ENTITY.get(), MatildaEggRenderer::new);
        event.registerEntityRenderer(ModEntities.DUMMY.get(), DummyRenderer::new);
        event.registerEntityRenderer(ModEntities.DUMMY_PROJECTILE.get(), DummyRenderer::new);


        event.registerBlockEntityRenderer(ModBlockEntities.SLING_SHOT.get(), SlingshotRenderer::new);

    }

    @SubscribeEvent
    public static void onRegisterParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.LEMON_BIRD_TRAIL.get(), BirdTrailParticle.Provider::createLiving);
        event.registerSpriteSet(ModParticles.LEMON_BIRD_TRAIL_PREVIEW.get(), BirdTrailParticle.Provider::createPreview);

    }

    @SubscribeEvent
    public static void onEntityRenderersRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BirdSlingshotModel.BODY_LAYER, BirdSlingshotModel::bodyLayer);
        event.registerLayerDefinition(BirdSlingshotModel.SUPPORT_LAYER, BirdSlingshotModel::supportLayer);
        event.registerLayerDefinition(BirdSlingshotModel.STRETCH_LAYER, BirdSlingshotModel::stretchLayer);

    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event)
    {
        event.register(ModKeyBinds.BIRD_ABILITY);
        event.register(ModKeyBinds.SLINGSHOT_LAUNCH);
    }


}
