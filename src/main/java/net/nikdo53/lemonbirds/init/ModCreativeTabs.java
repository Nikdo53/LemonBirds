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
            .icon(() -> new ItemStack(ModItems.RED_BIRD.get()))
            .displayItems((parameters, output) -> {
                ModItems.ITEMS.getEntries()
                        .forEach(item -> output.accept(item.get()));
            }).build());

}
