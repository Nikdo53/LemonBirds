package net.nikdo53.lemonbirds.init;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.entities.AbstractLemonBirdEntity;

import java.util.Map;

public interface ModDataMaps {
    DataMapType<Item, Map<Either<TagKey<Block>, Block>, Double>> BIRD_DESTROY_DATA = DataMapType.builder(
            LemonBirds.loc("destroy_data_overrides"), Registries.ITEM, AbstractLemonBirdEntity.DESTROY_EFFECTIVITY_CODEC).build();

}
