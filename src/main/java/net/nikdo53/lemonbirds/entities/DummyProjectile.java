package net.nikdo53.lemonbirds.entities;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.nikdo53.lemonbirds.init.ModBlockTags;
import net.nikdo53.lemonbirds.init.ModEntities;
import net.nikdo53.lemonbirds.init.ModParticles;

public class DummyProjectile extends ThrowableProjectile {
    public DummyProjectile(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public DummyProjectile(double x, double y, double z, Level level) {
        super(ModEntities.DUMMY_PROJECTILE.get(), x, y, z, level);
    }

    public DummyProjectile( Level level) {
        super(ModEntities.DUMMY_PROJECTILE.get(), level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (level().getBlockState(result.getBlockPos()).is(ModBlockTags.BIRD_BREAKABLE)) discard();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()){
            Vec3 pos = this.position();
            level().addParticle(ModParticles.LEMON_BIRD_TRAIL_PREVIEW.get(), true, pos.x, pos.y, pos.z, 0, 0,0);
        }
        if (level().isOutsideBuildHeight(getOnPos())){
            discard();
        }
    }
}
