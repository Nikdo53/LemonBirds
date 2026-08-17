package net.nikdo53.lemonbirds.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.blocks.BirdSlingshotBlockEntity;
import net.nikdo53.lemonbirds.network.ActivateLemonBirdPayload;
import net.nikdo53.lemonbirds.network.SlingshotRotationPayload;
import net.nikdo53.lemonbirds.network.SlingshotRotationSyncPayload;
import net.nikdo53.lemonbirds.network.SlingshotKeyPressPayload;
import net.nikdo53.lemonbirds.util.LateTickOperation;

@EventBusSubscriber(modid = LemonBirds.MOD_ID)
public class ServerEvents {

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();

        if (!(level instanceof ServerLevel serverLevel)) return;

        LateTickOperation.run(serverLevel);
    }

    /**
     * A player aiming a slingshot has their camera out at the pouch, blocks away from where they are stood and often
     * through the frame or a wall, and every key within reach is aiming something. Nothing they press should reach
     * the world until they step off.
     * <p>
     * These fire on both sides - the client is stopped earlier still in
     * {@link net.nikdo53.lemonbirds.events.ClientEvents#onInteractionKey}, but the server is the one that decides.
     */
    private static void cancelWhileOnSlingshot(Player player, ICancellableEvent event) {
        if (BirdSlingshotBlockEntity.isControllingSlingshot(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        cancelWhileOnSlingshot(event.getEntity(), event);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        cancelWhileOnSlingshot(event.getEntity(), event);
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        cancelWhileOnSlingshot(event.getEntity(), event);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        cancelWhileOnSlingshot(event.getEntity(), event);
    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        cancelWhileOnSlingshot(event.getEntity(), event);
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        cancelWhileOnSlingshot(event.getEntity(), event);
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                ActivateLemonBirdPayload.TYPE,
                ActivateLemonBirdPayload.STREAM_CODEC,
                ActivateLemonBirdPayload::handle
        );

        registrar.playToServer(
                SlingshotKeyPressPayload.TYPE,
                SlingshotKeyPressPayload.STREAM_CODEC,
                SlingshotKeyPressPayload::handle
        );

        registrar.playToServer(
                SlingshotRotationPayload.TYPE,
                SlingshotRotationPayload.STREAM_CODEC,
                SlingshotRotationPayload::handle
        );

        registrar.playToClient(
                SlingshotRotationSyncPayload.TYPE,
                SlingshotRotationSyncPayload.STREAM_CODEC,
                SlingshotRotationSyncPayload::handle
        );


    }

}
