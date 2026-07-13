package net.nikdo53.lemonbirds.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nikdo53.lemonbirds.LemonBirds;

public interface ModBlocks {
    DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LemonBirds.MOD_ID);

    DeferredBlock<Block> TEST_BLOCK = BLOCKS.register("test_block", () -> new Block(BlockBehaviour.Properties.of()));

}
