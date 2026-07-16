package net.nikdo53.lemonbirds;

import net.minecraft.resources.ResourceLocation;
import net.nikdo53.lemonbirds.init.*;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(LemonBirds.MOD_ID)
public class LemonBirds {
    public static final String MOD_ID = "lemonbirds";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LemonBirds(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
        ModParticles.PARTICLES.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModDataAttachments.ATTACHMENT_TYPES.register(modEventBus);
    }


    public static ResourceLocation loc(String path){
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
