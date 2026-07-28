package net.nikdo53.lemonbirds.entities;

import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.nikdo53.lemonbirds.init.ModEntities;
import net.nikdo53.lemonbirds.init.ModItems;

import java.util.List;

public class BlueLemonBirdEntity extends AbstractLemonBirdEntity{
    public BlueLemonBirdEntity(Level level, Position pos) {
        super(ModEntities.BLUE_LEMON_BIRD.get(), level, pos);
    }

    public BlueLemonBirdEntity(EntityType<? extends AbstractLemonBirdEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public DestroyEffectivity getDestroyEffectivity() {
        return new DestroyEffectivity(0.55, 0, 0.9, 0.5);
    }

    @Override
    protected void activateAbility(float xRot, float yRot) {
        for (float yRotAdd : new float[] {15.0f, -15.0f}){
                AbstractLemonBirdEntity projectile = ModItems.BLUE_BIRD.get().asProjectile(level(), getPosition(1), this.getMotionDirection());
                projectile.shootFromRotation(this, xRot, yRot + yRotAdd, 0.0f, 1.5f, 1.0f);
                level().addFreshEntity(projectile);
        }

    }

    public BlueLemonBirdEntity(Level level, Player player) {
        super(ModEntities.BLUE_LEMON_BIRD.get(), level, player);
    }

}
