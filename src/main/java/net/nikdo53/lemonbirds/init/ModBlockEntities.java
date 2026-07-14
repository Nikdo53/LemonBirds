package net.nikdo53.lemonbirds.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.blocks.FallingBirdBlockEntity;

import java.util.function.Supplier;

public interface ModBlockEntities {
    DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, LemonBirds.MOD_ID);

    Supplier<BlockEntityType<FallingBirdBlockEntity>> FALLING_BIRD = BLOCK_ENTITIES.register("falling_bird",
            () -> BlockEntityType.Builder.of(FallingBirdBlockEntity::new, ModBlocks.FALLING_LEMON_BIRD_BLOCK.get()).build(null));

}
