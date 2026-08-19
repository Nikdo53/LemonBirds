package net.nikdo53.lemonbirds.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.nikdo53.lemonbirds.init.ModEntities;
import net.nikdo53.lemonbirds.init.ModItems;

public class MatildaEggProjectile extends ThrowableItemProjectile {

    public MatildaEggProjectile(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public MatildaEggProjectile(double x, double y, double z, Level level) {
        super(ModEntities.MATILDA_EGG_ENTITY.get(), x, y, z, level);
    }

    public MatildaEggProjectile( LivingEntity shooter, Level level) {
        super(ModEntities.MATILDA_EGG_ENTITY.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.LEMON_EGG.asItem();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        Level level = level();
        if (!level.isClientSide) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 2, false, Level.ExplosionInteraction.MOB);
            this.discard();
        }
    }
}
