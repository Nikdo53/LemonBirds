package net.nikdo53.lemonbirds.entities;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.nikdo53.lemonbirds.init.ModEntities;

public class DummyProjectile extends ThrowableProjectile {
    public DummyProjectile(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public DummyProjectile(double x, double y, double z, Level level) {
        super(ModEntities.DUMMY_PROJECTILE.get(), x, y, z, level);
    }

    public DummyProjectile(LivingEntity shooter, Level level) {
        super(ModEntities.DUMMY_PROJECTILE.get(), shooter, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }
}
