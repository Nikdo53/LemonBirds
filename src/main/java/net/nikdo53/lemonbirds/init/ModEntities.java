package net.nikdo53.lemonbirds.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.entities.*;

import java.util.function.Supplier;

public interface ModEntities {
    DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, LemonBirds.MOD_ID);

    Supplier<EntityType<RedLemonBirdEntity>> RED_LEMON_BIRD = ENTITIES.register("red_lemon_bird", () -> EntityType.Builder.<RedLemonBirdEntity>of(RedLemonBirdEntity::new, MobCategory.MISC)
            .sized(1.0f, 1.0f)
            .build(LemonBirds.loc("red_lemon_bird").toString()));

    Supplier<EntityType<YellowLemonBirdEntity>> YELLOW_LEMON_BIRD = ENTITIES.register("yellow_lemon_bird", () -> EntityType.Builder.<YellowLemonBirdEntity>of(YellowLemonBirdEntity::new, MobCategory.MISC)
            .sized(0.9f, 0.75f)
            .build(LemonBirds.loc("yellow_lemon_bird").toString()));

    Supplier<EntityType<BlueLemonBirdEntity>> BLUE_LEMON_BIRD = ENTITIES.register("blue_lemon_bird", () -> EntityType.Builder.<BlueLemonBirdEntity>of(BlueLemonBirdEntity::new, MobCategory.MISC)
            .sized(0.5f, 0.5f)
            .build(LemonBirds.loc("blue_lemon_bird").toString()));

    Supplier<EntityType<BombLemonBirdEntity>> BOMB_LEMON_BIRD = ENTITIES.register("bomb_lemon_bird", () -> EntityType.Builder.<BombLemonBirdEntity>of(BombLemonBirdEntity::new, MobCategory.MISC)
            .sized(1.0f, 1.0f)
            .build(LemonBirds.loc("bomb_lemon_bird").toString()));

    Supplier<EntityType<MatildaLemonBirdEntity>> MATILDA_LEMON_BIRD = ENTITIES.register("matilda_lemon_bird", () -> EntityType.Builder.<MatildaLemonBirdEntity>of(MatildaLemonBirdEntity::new, MobCategory.MISC)
            .sized(1.0f, 1.5f)
            .build(LemonBirds.loc("matilda_lemon_bird").toString()));



    Supplier<EntityType<FakeLemonBirdEntity>> RED_SCREAM_ENTITY = ENTITIES.register("red_scream_entity", () -> EntityType.Builder.<FakeLemonBirdEntity>of(FakeLemonBirdEntity::new, MobCategory.MISC)
            .sized(3.5f, 2.0f)
            .build(LemonBirds.loc("red_scream_entity").toString()));

    Supplier<EntityType<MatildaEggProjectile>> MATILDA_EGG_ENTITY = ENTITIES.register("matilda_egg_entity", () -> EntityType.Builder.<MatildaEggProjectile>of(MatildaEggProjectile::new, MobCategory.MISC)
            .sized(0.5f, 0.5f)
            .build(LemonBirds.loc("matilda_egg_entity").toString()));



    ;
}
