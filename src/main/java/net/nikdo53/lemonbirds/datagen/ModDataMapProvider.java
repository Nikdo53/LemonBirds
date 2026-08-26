package net.nikdo53.lemonbirds.datagen;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.nikdo53.lemonbirds.init.ModBlockTags;
import net.nikdo53.lemonbirds.init.ModDataMaps;
import net.nikdo53.lemonbirds.init.ModItems;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModDataMapProvider extends DataMapProvider {
    protected ModDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        this.builder(ModDataMaps.BIRD_DESTROY_DATA)
                .add(ModItems.BOMB_LEMON,
                        Map.of(
                                Either.right(Blocks.OBSIDIAN), 0.1,
                                Either.right(Blocks.SLIME_BLOCK), 0.9,
                                Either.left(ModBlockTags.LEMON_BIRDS_GLASS), 0.7
                        ), false);
    }
}
