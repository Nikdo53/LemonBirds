package net.nikdo53.lemonbirds.entities;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Position;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.nikdo53.lemonbirds.init.ModEntities;

import java.util.List;

public class RedLemonBirdEntity extends AbstractLemonBirdEntity{
    public RedLemonBirdEntity(Level level, Position pos) {
        super(ModEntities.RED_LEMON_BIRD.get(), level, pos);
    }

    public RedLemonBirdEntity(EntityType<? extends AbstractLemonBirdEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public DestroyEffectivity getDestroyEffectivity() {
        return new DestroyEffectivity(0.8, 0.20, 0.45, 0.6);
    }

    @Override
    protected void activateAbility() {


    }

    public RedLemonBirdEntity(Level level, Player player) {
        super(ModEntities.RED_LEMON_BIRD.get(), level, player);
    }

}
