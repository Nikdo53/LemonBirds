package net.nikdo53.lemonbirds.entities;

import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FakeLemonBirdEntity extends AbstractLemonBirdEntity{
    public FakeLemonBirdEntity(EntityType<? extends AbstractLemonBirdEntity> entityType, Level level, Position pos) {
        super(entityType, level, pos);
    }

    public FakeLemonBirdEntity(EntityType<? extends AbstractLemonBirdEntity> entityType, Level level) {
        super(entityType, level);
    }

    public FakeLemonBirdEntity(EntityType<? extends AbstractLemonBirdEntity> entityType, Level level, Player player) {
        super(entityType, level, player);
    }

    @Override
    public DestroyEffectivity getDestroyEffectivity() {
        return new DestroyEffectivity(0, 0, 0.2, 0);
    }

    @Override
    protected void activateAbility() {

    }
}
