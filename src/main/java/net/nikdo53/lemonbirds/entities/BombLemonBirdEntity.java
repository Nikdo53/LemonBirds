package net.nikdo53.lemonbirds.entities;

import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.nikdo53.lemonbirds.init.ModEntities;

import java.util.List;

public class BombLemonBirdEntity extends AbstractLemonBirdEntity{
    public BombLemonBirdEntity(Level level, Position pos) {
        super(ModEntities.BOMB_LEMON_BIRD.get(), level, pos);
    }

    public BombLemonBirdEntity(EntityType<? extends AbstractLemonBirdEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public DestroyEffectivity getDestroyEffectivity() {
        return new DestroyEffectivity(0.45, 0.70, 0.2, 0.4);
    }

    @Override
    protected void activateAbility() {
        level().explode(this, this.getX(), this.getY(), this.getZ(), 2.0f, Level.ExplosionInteraction.MOB);
    }

    public BombLemonBirdEntity(Level level, Player player) {
        super(ModEntities.BOMB_LEMON_BIRD.get(), level, player);
    }

}
