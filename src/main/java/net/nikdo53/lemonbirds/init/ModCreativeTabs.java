package net.nikdo53.lemonbirds.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nikdo53.lemonbirds.LemonBirds;

public interface ModCreativeTabs {
     DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), LemonBirds.MOD_ID);

    DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = TABS.register("lemonbirds_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("lemonbirds.creative_tab"))
            .icon(() -> new ItemStack(ModItems.RED_LEMON.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.RED_LEMON.get());
                output.accept(ModItems.BOMB_LEMON.get());
                output.accept(ModItems.YELLOW_LEMON.get());
                output.accept(ModItems.BLUE_LEMON.get());
                output.accept(ModItems.WHITE_LEMON.get());
                output.accept(ModItems.BIG_LEMON.get());

                addSeparator(output);
                output.accept(ModBlocks.SLING_SHOT.toStack());
                output.accept(ModBlocks.LEMON_TNT.toStack());
                output.accept(ModItems.LEMON_EGG.toStack());
                addSeparator(output);

                output.accept(ModBlocks.BAD_PIG.toStack());
                output.accept(ModBlocks.CORPORAL_PIG.toStack());
                output.accept(ModBlocks.FOREMAN_PIG_BOSS.toStack());
                output.accept(ModBlocks.CHEF_PIG_BOSS.toStack());
                output.accept(ModBlocks.KING_PIG_BOSS.toStack());


            }).build());


    static void addSeparator(CreativeModeTab.Output output) {
        for (int i = 0; i < 9; i++) {
            output.accept(ModItems.EMPTY_ITEM.toStack());
        }
    }
}
