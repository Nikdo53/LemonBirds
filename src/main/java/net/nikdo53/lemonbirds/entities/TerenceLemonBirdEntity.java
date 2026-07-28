package net.nikdo53.lemonbirds.entities;

import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.nikdo53.lemonbirds.init.ModEntities;

public class TerenceLemonBirdEntity extends AbstractLemonBirdEntity{
    public TerenceLemonBirdEntity(Level level, Position pos) {
        super(ModEntities.TERENCE_LEMON_BIRD.get(), level, pos);
    }

    public TerenceLemonBirdEntity(EntityType<? extends AbstractLemonBirdEntity> entityType, Level level) {
        super(entityType, level);
    }

    public TerenceLemonBirdEntity(Level level, Player player) {
        super(ModEntities.TERENCE_LEMON_BIRD.get(), level, player);
    }

    @Override
    public DestroyEffectivity getDestroyEffectivity() {
        return new DestroyEffectivity(0.95, 0.90, 0.98, 0.98);
    }

    @Override
    protected void activateAbility(float xRot, float yRot) {

    }
}
