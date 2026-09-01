package net.nikdo53.lemonbirds.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.nikdo53.lemonbirds.LemonBirds;

public interface ModBlockTags {

    TagKey<Block> BIRD_BREAKABLE = TagKey.create(Registries.BLOCK, LemonBirds.loc("bird_breakable"));

    TagKey<Block> LEMON_BIRDS_WOOD = TagKey.create(Registries.BLOCK, LemonBirds.loc("lemon_birds_wood"));
    TagKey<Block> LEMON_BIRDS_STONE = TagKey.create(Registries.BLOCK, LemonBirds.loc("lemon_birds_stone"));
    TagKey<Block> LEMON_BIRDS_GLASS = TagKey.create(Registries.BLOCK, LemonBirds.loc("lemon_birds_glass"));
    TagKey<Block> LEMON_BIRDS_HAY = TagKey.create(Registries.BLOCK, LemonBirds.loc("lemon_birds_hay"));

    TagKey<Block> LEMON_BIRD = TagKey.create(Registries.BLOCK, LemonBirds.loc("lemon_bird"));
    TagKey<Block> BAD_PIGS = TagKey.create(Registries.BLOCK, LemonBirds.loc("bad_pig"));
    TagKey<Block> BAD_PIG_BOSS = TagKey.create(Registries.BLOCK, LemonBirds.loc("bad_pig_boss"));



}
