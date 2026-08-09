package net.nikdo53.lemonbirds.init;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.blocks.BadPigBlock;
import net.nikdo53.lemonbirds.blocks.BirdSlingshotBlock;
import net.nikdo53.lemonbirds.blocks.FallingBirdBlock;
import net.nikdo53.lemonbirds.items.SublevelBlockItem;

import java.util.function.Supplier;

public interface ModBlocks {
    DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LemonBirds.MOD_ID);

    DeferredBlock<Block> FALLING_LEMON_BIRD_BLOCK = BLOCKS.register("falling_lemon_bird_block", () -> new FallingBirdBlock(BlockBehaviour.Properties.of().noOcclusion()));

    DeferredBlock<Block> RED_BIRD_BLOCK = BLOCKS.register("red_bird_block", () -> new FallingBirdBlock(BlockBehaviour.Properties.of().noOcclusion()));
    DeferredBlock<Block> YELLOW_BIRD_BLOCK = BLOCKS.register("yellow_bird_block", () -> new FallingBirdBlock(BlockBehaviour.Properties.of().noOcclusion()));
    DeferredBlock<Block> BLUE_BIRD_BLOCK = BLOCKS.register("blue_bird_block", () -> new FallingBirdBlock(BlockBehaviour.Properties.of().noOcclusion()));
    DeferredBlock<Block> BOMB_BIRD_BLOCK = BLOCKS.register("bomb_bird_block", () -> new FallingBirdBlock(BlockBehaviour.Properties.of().noOcclusion()));
    DeferredBlock<Block> MATILDA_BIRD_BLOCK = BLOCKS.register("matilda_bird_block", () -> new FallingBirdBlock(BlockBehaviour.Properties.of().noOcclusion(), FallingBirdBlock.MATILDA_SHAPE));
    DeferredBlock<Block> TERENCE_BIRD_BLOCK = BLOCKS.register("terence_bird_block", () -> new FallingBirdBlock(BlockBehaviour.Properties.of().noOcclusion(), FallingBirdBlock.TERENCE_SHAPE));

    DeferredBlock<Block> SLING_SHOT = registerWithItem("bird_sling_shot", () -> new BirdSlingshotBlock(BlockBehaviour.Properties.of().noOcclusion()));

    DeferredBlock<Block> BAD_PIG = registerSableItem("bad_pig", () -> new BadPigBlock(BlockBehaviour.Properties.of().noOcclusion().emissiveRendering(ModBlocks::always), BadPigBlock.ONE_BLOCK));
    DeferredBlock<Block> CORPORAL_PIG = registerSableItem("corporal_pig", () -> new BadPigBlock(BlockBehaviour.Properties.of().noOcclusion().emissiveRendering(ModBlocks::always), BadPigBlock.ONE_BLOCK));
    DeferredBlock<Block> FOREMAN_PIG_BOSS = registerSableItem("foreman_pig_boss", () -> new BadPigBlock(BlockBehaviour.Properties.of().noOcclusion().emissiveRendering(ModBlocks::always), BadPigBlock.FOREMAN_SHAPE));
    DeferredBlock<Block> CHEF_PIG_BOSS = registerSableItem("chef_pig_boss", () -> new BadPigBlock(BlockBehaviour.Properties.of().noOcclusion().emissiveRendering(ModBlocks::always), BadPigBlock.CHEF_SHAPE));
    DeferredBlock<Block> KING_PIG_BOSS = registerSableItem("king_pig_boss", () -> new BadPigBlock(BlockBehaviour.Properties.of().noOcclusion().emissiveRendering(ModBlocks::always), BadPigBlock.KING_SHAPE));


    static <T extends Block> DeferredBlock<T> registerWithItem(String name, Supplier<T> block){
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties()));
        return toReturn;
    }

    static <T extends Block> DeferredBlock<T> registerSableItem(String name, Supplier<T> block){
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new SublevelBlockItem(toReturn.get(), new Item.Properties()));
        return toReturn;
    }

    private static boolean always(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return true;
    }



}
