package net.nikdo53.lemonbirds.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.entities.*;
import net.nikdo53.lemonbirds.items.BirdItem;
import net.nikdo53.lemonbirds.items.MatildaEggItem;
import net.nikdo53.lemonbirds.items.TestItem;

public interface ModItems {
    DeferredRegister.Items ITEMS = DeferredRegister.createItems(LemonBirds.MOD_ID);

    DeferredItem<BirdItem> RED_BIRD = ITEMS.register("red_bird", () -> new BirdItem(new Item.Properties(), ModBlocks.RED_BIRD_BLOCK, RedLemonBirdEntity::new, RedLemonBirdEntity::new));
    DeferredItem<BirdItem> BOMB_BIRD = ITEMS.register("bomb_bird", () -> new BirdItem(new Item.Properties(), ModBlocks.BOMB_BIRD_BLOCK, BombLemonBirdEntity::new, BombLemonBirdEntity::new));
    DeferredItem<BirdItem> YELLOW_BIRD = ITEMS.register("yellow_bird", () -> new BirdItem(new Item.Properties(), ModBlocks.YELLOW_BIRD_BLOCK, YellowLemonBirdEntity::new, YellowLemonBirdEntity::new));
    DeferredItem<BirdItem> BLUE_BIRD = ITEMS.register("blue_bird", () -> new BirdItem(new Item.Properties(), ModBlocks.BLUE_BIRD_BLOCK, BlueLemonBirdEntity::new, BlueLemonBirdEntity::new));
    DeferredItem<BirdItem> MATILDA_BIRD = ITEMS.register("matilda_bird", () -> new BirdItem(new Item.Properties(), ModBlocks.MATILDA_BIRD_BLOCK, MatildaLemonBirdEntity::new, MatildaLemonBirdEntity::new));
    DeferredItem<BirdItem> TERENCE_BIRD = ITEMS.register("terence_bird", () -> new BirdItem(new Item.Properties(), ModBlocks.TERENCE_BIRD_BLOCK, TerenceLemonBirdEntity::new, TerenceLemonBirdEntity::new));

    AbstractLemonBirdEntity.DestroyEffectivity SCREAM_EFFECTIVITY = new AbstractLemonBirdEntity.DestroyEffectivity(0.8, 0.8, 0.8, 0.8, false);

    DeferredItem<BirdItem> RED_SCREAM = ITEMS.register("red_bird_scream", () -> new BirdItem(new Item.Properties(), null,
            (level, player) -> new FakeLemonBirdEntity(ModEntities.RED_SCREAM_ENTITY.get(), level, player, SCREAM_EFFECTIVITY),
            (level, position) -> new FakeLemonBirdEntity(ModEntities.RED_SCREAM_ENTITY.get(), level, position, SCREAM_EFFECTIVITY)));

    DeferredItem<Item> MATILDA_EGG = ITEMS.register("matilda_egg", () -> new MatildaEggItem(new Item.Properties()));

    DeferredItem<Item> BOMB_BIRD_ORANGE = ITEMS.register("bomb_bird_orange", () -> new Item(new Item.Properties()));

  //  DeferredItem<Item> TEST = ITEMS.register("test_item", () -> new TestItem(new Item.Properties()));

    DeferredItem<Item> EMPTY_ITEM = ITEMS.register("empty_item", () -> new Item(new Item.Properties()));

}
