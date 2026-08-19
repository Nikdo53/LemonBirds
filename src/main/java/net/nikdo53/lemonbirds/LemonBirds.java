package net.nikdo53.lemonbirds;

import net.mcexpanded.fancytabsections.FancyTabSections;
import net.mcexpanded.fancytabsections.Section.SectionColored;
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


        FancyTabSections.addSection(loc("lemonbirds_tab"),
                new SectionColored(loc("birds"))
                        .add(ModItems.RED_LEMON)
                        .add(ModItems.BOMB_LEMON)
                        .add(ModItems.YELLOW_LEMON)
                        .add(ModItems.BLUE_LEMON)
                        .add(ModItems.WHITE_LEMON)
                        .add(ModItems.BIG_LEMON)
                        .setBannerColor(0xFFFF0000)
        );

        FancyTabSections.addSection(loc("lemonbirds_tab"),
                new SectionColored(loc("misc"))
                        .add(ModBlocks.SLING_SHOT)
                        .add(ModBlocks.LEMON_TNT)
                        .add(ModItems.LEMON_EGG)
                        .setBannerColor(0xFF333333)
        );

        FancyTabSections.addSection(loc("lemonbirds_tab"),
                new SectionColored(loc("pigs"))
                        .add(ModBlocks.BAD_PIG)
                        .add(ModBlocks.BUILDER_BAD_PIG)
                        .add(ModBlocks.CORPORAL_PIG)
                        .add(ModBlocks.FOREMAN_PIG_BOSS)
                        .add(ModBlocks.CHEF_PIG_BOSS)
                        .add(ModBlocks.KING_PIG_BOSS)
                        .setBannerColor(0xFF00FF00)
        );




    }


    public static ResourceLocation loc(String path){
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
