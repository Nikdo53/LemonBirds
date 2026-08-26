package net.nikdo53.lemonbirds.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.nikdo53.lemonbirds.LemonBirds;
import org.lwjgl.system.macosx.CGEventTapCallBack;

@EventBusSubscriber(modid = LemonBirds.MOD_ID)
public class ModDataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        gen.addProvider(event.includeServer(), new ModBlockTagsProvider(output, event.getLookupProvider(), existingFileHelper));
        gen.addProvider(event.includeServer(), new ModDataMapProvider(output, event.getLookupProvider()));

    }

}
