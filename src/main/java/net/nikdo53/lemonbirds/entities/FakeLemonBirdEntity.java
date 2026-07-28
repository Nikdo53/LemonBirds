package net.nikdo53.lemonbirds.entities;

import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FakeLemonBirdEntity extends AbstractLemonBirdEntity{
    public DestroyEffectivity destroyEffectivity;

    public FakeLemonBirdEntity(EntityType<? extends AbstractLemonBirdEntity> entityType, Level level, Position pos, DestroyEffectivity destroyEffectivity) {
        super(entityType, level, pos);
        this.destroyEffectivity = destroyEffectivity;
    }

    public FakeLemonBirdEntity(EntityType<? extends AbstractLemonBirdEntity> entityType, Level level) {
        super(entityType, level);
    }

    public FakeLemonBirdEntity(EntityType<? extends AbstractLemonBirdEntity> entityType, Level level, Player player, DestroyEffectivity destroyEffectivity) {
        super(entityType, level, player);
        this.destroyEffectivity = destroyEffectivity;
    }

    @Override
    public DestroyEffectivity getDestroyEffectivity() {
        return destroyEffectivity != null ? this.destroyEffectivity : new DestroyEffectivity(0, 0, 0.0, 0);
    }

    @Override
    protected void activateAbility(float xRot, float yRot) {

    }
}
