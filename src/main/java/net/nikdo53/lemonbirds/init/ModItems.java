package net.nikdo53.lemonbirds.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.entities.BlueLemonBirdEntity;
import net.nikdo53.lemonbirds.entities.BombLemonBirdEntity;
import net.nikdo53.lemonbirds.entities.RedLemonBirdEntity;
import net.nikdo53.lemonbirds.entities.YellowLemonBirdEntity;
import net.nikdo53.lemonbirds.items.BirdItem;
import net.nikdo53.lemonbirds.items.TestItem;

public interface ModItems {
    DeferredRegister.Items ITEMS = DeferredRegister.createItems(LemonBirds.MOD_ID);

    DeferredItem<Item> RED_BIRD = ITEMS.register("red_bird", () -> new BirdItem(new Item.Properties(), RedLemonBirdEntity::new, RedLemonBirdEntity::new));
    DeferredItem<Item> BOMB_BIRD = ITEMS.register("bomb_bird", () -> new BirdItem(new Item.Properties(), BombLemonBirdEntity::new, BombLemonBirdEntity::new));
    DeferredItem<Item> YELLOW_BIRD = ITEMS.register("yellow_bird", () -> new BirdItem(new Item.Properties(), YellowLemonBirdEntity::new, YellowLemonBirdEntity::new));
    DeferredItem<Item> BLUE_BIRD = ITEMS.register("blue_bird", () -> new BirdItem(new Item.Properties(), BlueLemonBirdEntity::new, BlueLemonBirdEntity::new));

    DeferredItem<Item> TEST = ITEMS.register("test_item", () -> new TestItem(new Item.Properties()));


}
