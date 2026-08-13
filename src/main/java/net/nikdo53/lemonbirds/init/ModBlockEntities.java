package net.nikdo53.lemonbirds.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.blocks.BirdSlingshotBlockEntity;
import net.nikdo53.lemonbirds.blocks.BombFallingBirdBlockEntity;
import net.nikdo53.lemonbirds.blocks.FallingBirdBlockEntity;

import java.util.function.Supplier;

public interface ModBlockEntities {
    DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, LemonBirds.MOD_ID);

    Supplier<BlockEntityType<FallingBirdBlockEntity>> FALLING_BIRD = BLOCK_ENTITIES.register("falling_bird",
            () -> BlockEntityType.Builder.of(FallingBirdBlockEntity::new, getValidBlock()).build(null));

    Supplier<BlockEntityType<BirdSlingshotBlockEntity>> SLING_SHOT = BLOCK_ENTITIES.register("bird_slingshot",
            () -> BlockEntityType.Builder.of(BirdSlingshotBlockEntity::new, ModBlocks.SLING_SHOT.get()).build(null));

    Supplier<BlockEntityType<BombFallingBirdBlockEntity>> FALLING_BIRD_BOMB = BLOCK_ENTITIES.register("falling_bird_bomb",
            () -> BlockEntityType.Builder.of(BombFallingBirdBlockEntity::new, getValidBlock()).build(null));

    static Block[] getValidBlock(){
        return ModBlocks.BLOCKS.getEntries().stream().map(Supplier::get).toList().toArray(new Block[0]);
    }

}
