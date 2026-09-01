package net.nikdo53.lemonbirds.datagen;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.index.SableTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.init.ModBlockTags;
import net.nikdo53.lemonbirds.init.ModBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, LemonBirds.MOD_ID, existingFileHelper);
    }

    public static final TagKey<Block> FRAGILE = TagKey.create(
            Registries.BLOCK,
            Sable.sablePath("fragile")
    );

    public static final TagKey<Block> BOUNCY = TagKey.create(
            Registries.BLOCK,
            Sable.sablePath("bouncy")
    );


    @Override
    @SuppressWarnings("unchecked")
    protected void addTags(HolderLookup.Provider provider) {
       tag(FRAGILE).addTags(ModBlockTags.LEMON_BIRDS_WOOD, ModBlockTags.LEMON_BIRDS_GLASS);

       tag(ModBlockTags.LEMON_BIRDS_WOOD).addTags(BlockTags.MINEABLE_WITH_AXE);
       tag(ModBlockTags.LEMON_BIRDS_STONE).addTags(BlockTags.BASE_STONE_OVERWORLD, BlockTags.STONE_BRICKS, Tags.Blocks.COBBLESTONES);
       tag(ModBlockTags.LEMON_BIRDS_GLASS).addTags(Tags.Blocks.GLASS_BLOCKS, Tags.Blocks.GLASS_PANES);
       tag(ModBlockTags.LEMON_BIRDS_HAY).addTag(BlockTags.WOOL).add(Blocks.HAY_BLOCK).addTag(BlockTags.LEAVES).add(Blocks.TNT).add(ModBlocks.LEMON_TNT.get());

        for (DeferredHolder<Block, ? extends Block> entry : ModBlocks.BLOCKS.getEntries()) {
            tag(ModBlockTags.LEMON_BIRD).add(entry.get());
        }

        tag(ModBlockTags.BIRD_BREAKABLE).addTags(ModBlockTags.LEMON_BIRDS_WOOD, ModBlockTags.LEMON_BIRDS_STONE, ModBlockTags.LEMON_BIRDS_GLASS, ModBlockTags.LEMON_BIRDS_HAY);

        tag(BOUNCY).addTags(ModBlockTags.LEMON_BIRD);
        tag(ModBlockTags.BAD_PIGS).add(ModBlocks.BAD_PIG.get(), ModBlocks.BUILDER_BAD_PIG.get(), ModBlocks.CORPORAL_PIG.get(),
                ModBlocks.KING_PIG_BOSS.get(), ModBlocks.FOREMAN_PIG_BOSS.get(), ModBlocks.CHEF_PIG_BOSS.get());
    }
}
