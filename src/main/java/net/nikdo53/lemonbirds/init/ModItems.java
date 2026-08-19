package net.nikdo53.lemonbirds.init;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.entities.*;
import net.nikdo53.lemonbirds.items.BirdItem;
import net.nikdo53.lemonbirds.items.MatildaEggItem;

public interface ModItems {
    DeferredRegister.Items ITEMS = DeferredRegister.createItems(LemonBirds.MOD_ID);

    DeferredItem<BirdItem> RED_LEMON = ITEMS.register("red_lemon", () -> new BirdItem(new Item.Properties(), ModBlocks.RED_LEMON_BLOCK, RedLemonBirdEntity::new, RedLemonBirdEntity::new));
    DeferredItem<BirdItem> BOMB_LEMON = ITEMS.register("bomb_lemon", () -> new BirdItem(new Item.Properties(), ModBlocks.BOMB_LEMON_BLOCK, BombLemonBirdEntity::new, BombLemonBirdEntity::new));
    DeferredItem<BirdItem> YELLOW_LEMON = ITEMS.register("yellow_lemon", () -> new BirdItem(new Item.Properties(), ModBlocks.YELLOW_LEMON_BLOCK, YellowLemonBirdEntity::new, YellowLemonBirdEntity::new));
    DeferredItem<BirdItem> BLUE_LEMON = ITEMS.register("blue_lemon", () -> new BirdItem(new Item.Properties(), ModBlocks.BLUE_LEMON_BLOCK, BlueLemonBirdEntity::new, BlueLemonBirdEntity::new));
    DeferredItem<BirdItem> WHITE_LEMON = ITEMS.register("white_lemon", () -> new BirdItem(new Item.Properties(), ModBlocks.WHITE_LEMON_BLOCK, MatildaLemonBirdEntity::new, MatildaLemonBirdEntity::new));
    DeferredItem<BirdItem> BIG_LEMON = ITEMS.register("big_lemon", () -> new BirdItem(new Item.Properties(), ModBlocks.BIG_LEMON_BLOCK, TerenceLemonBirdEntity::new, TerenceLemonBirdEntity::new));

    AbstractLemonBirdEntity.DestroyEffectivity SCREAM_EFFECTIVITY = new AbstractLemonBirdEntity.DestroyEffectivity(0.8, 0.8, 0.8, 0.8, false);

    DeferredItem<BirdItem> RED_SCREAM = ITEMS.register("red_lemon_scream", () -> new BirdItem(new Item.Properties(), null,
            (level, player) -> new FakeLemonBirdEntity(ModEntities.RED_SCREAM_ENTITY.get(), level, player, SCREAM_EFFECTIVITY),
            (level, position) -> new FakeLemonBirdEntity(ModEntities.RED_SCREAM_ENTITY.get(), level, position, SCREAM_EFFECTIVITY)));

    DeferredItem<Item> LEMON_EGG = ITEMS.register("lemon_egg", () -> new MatildaEggItem(new Item.Properties()));

    DeferredItem<Item> BOMB_LEMON_ORANGE = ITEMS.register("bomb_lemon_orange", () -> new Item(new Item.Properties()));

  //  DeferredItem<Item> TEST = ITEMS.register("test_item", () -> new TestItem(new Item.Properties()));

    DeferredItem<Item> EMPTY_ITEM = ITEMS.register("empty_item", () -> new Item(new Item.Properties()));

}
