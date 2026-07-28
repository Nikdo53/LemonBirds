package net.nikdo53.lemonbirds.entities;

import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.nikdo53.lemonbirds.init.ModEntities;
import net.nikdo53.lemonbirds.init.ModItems;

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
    protected void activateAbility(float xRot, float yRot) {

        AbstractLemonBirdEntity projectile = ModItems.RED_SCREAM.get().asProjectile(level(), getPosition(1), this.getMotionDirection());
        projectile.shootFromRotation(this, this.getXRot(), this.getYRot(), 0.0f, 1.5f, 1.0f);
        level().addFreshEntity(projectile);


        setDeltaMovement(getDeltaMovement().scale(0.75));

    }


    public RedLemonBirdEntity(Level level, Player player) {
        super(ModEntities.RED_LEMON_BIRD.get(), level, player);
    }

}
