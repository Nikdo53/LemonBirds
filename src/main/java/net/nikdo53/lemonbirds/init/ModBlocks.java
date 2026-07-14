package net.nikdo53.lemonbirds.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.blocks.FallingBirdBlock;
import net.nikdo53.lemonbirds.blocks.FallingBirdBlockNew;
import net.nikdo53.lemonbirds.blocks.LemonBirdBlock;

import java.util.function.Supplier;

public interface ModBlocks {
    DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LemonBirds.MOD_ID);

    DeferredBlock<Block> FALLING_LEMON_BIRD_BLOCK = BLOCKS.register("falling_lemon_bird_block", () -> new FallingBirdBlock(BlockBehaviour.Properties.of().noOcclusion()));

    DeferredBlock<Block> RED_BIRD_BLOCK = BLOCKS.register("red_bird_block", () -> new FallingBirdBlockNew(BlockBehaviour.Properties.of().noOcclusion()));
    DeferredBlock<Block> YELLOW_BIRD_BLOCK = BLOCKS.register("yellow_bird_block", () -> new FallingBirdBlockNew(BlockBehaviour.Properties.of().noOcclusion()));
    DeferredBlock<Block> BLUE_BIRD_BLOCK = BLOCKS.register("blue_bird_block", () -> new FallingBirdBlockNew(BlockBehaviour.Properties.of().noOcclusion()));
    DeferredBlock<Block> BOMB_BIRD_BLOCK = BLOCKS.register("bomb_bird_block", () -> new FallingBirdBlockNew(BlockBehaviour.Properties.of().noOcclusion()));
        DeferredBlock<Block> MATILDA_BIRD_BLOCK = BLOCKS.register("matilda_bird_block", () -> new FallingBirdBlockNew(BlockBehaviour.Properties.of().noOcclusion()));


    static <T extends Block> DeferredBlock<T> registerWithItem(String name, Supplier<T> block){
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> DeferredItem<Item> registerBlockItem(String name, DeferredBlock<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties()));
    }


}
