package net.nikdo53.lemonbirds.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.network.ActivateLemonBirdPayload;
import net.nikdo53.lemonbirds.network.SlingshotRotationPayload;
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


    }

}
