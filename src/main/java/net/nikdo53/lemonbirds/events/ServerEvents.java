package net.nikdo53.lemonbirds.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.util.LateTickOperation;

@EventBusSubscriber(modid = LemonBirds.MOD_ID)
public class ServerEvents {

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();

        if (!(level instanceof ServerLevel serverLevel)) return;

        for (int i = 0; i < LateTickOperation.SUB_LEVEL_OPERATIONS.size(); i++) {
            LateTickOperation lateTickOperation = LateTickOperation.SUB_LEVEL_OPERATIONS.get(i);
            if (lateTickOperation.tick <= 0) {
                lateTickOperation.operation.accept(serverLevel);
                LateTickOperation.SUB_LEVEL_OPERATIONS.remove(i);
                i--;
            } else {
                lateTickOperation.tick--;
            }
        }
        ;
    }
}
