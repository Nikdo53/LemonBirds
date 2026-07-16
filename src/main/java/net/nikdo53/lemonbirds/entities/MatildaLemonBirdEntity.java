package net.nikdo53.lemonbirds.entities;

import net.minecraft.core.Position;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.nikdo53.lemonbirds.init.ModEntities;
import net.nikdo53.lemonbirds.init.ModItems;

public class MatildaLemonBirdEntity extends AbstractLemonBirdEntity{
    private static final EntityDataAccessor<Integer> ACTIVE_ABILITY_TICKS = SynchedEntityData.defineId(
            MatildaLemonBirdEntity.class, EntityDataSerializers.INT
    );

    public MatildaLemonBirdEntity(Level level, Position pos) {
        super(ModEntities.MATILDA_LEMON_BIRD.get(), level, pos);
    }

    public MatildaLemonBirdEntity(EntityType<? extends AbstractLemonBirdEntity> entityType, Level level) {
        super(entityType, level);
    }

    public MatildaLemonBirdEntity(Level level, Player player) {
        super(ModEntities.MATILDA_LEMON_BIRD.get(), level, player);
    }

    @Override
    public DestroyEffectivity getDestroyEffectivity() {
        return new DestroyEffectivity(0.55, 0.25, 0.4, 0.5);
    }

    @Override
    protected void activateAbility() {
        if (level().isClientSide()) return;
        this.entityData.set(ACTIVE_ABILITY_TICKS, 20);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) return;

        int ticks = this.entityData.get(ACTIVE_ABILITY_TICKS);

        if (ticks % 10 == 0){
            shootEgg();
        }

        if (ticks >= 0) {
            this.entityData.set(ACTIVE_ABILITY_TICKS, ticks - 1);
        }
    }

    public void shootEgg(){
        Vec3 pos = this.position();
        Level level = level();
        Vec3 deltaMovement = getDeltaMovement();

        MatildaEggProjectile projectile = new MatildaEggProjectile(pos.x, pos.y, pos.z, level);
        projectile.setDeltaMovement(deltaMovement.x / 2f, -1, deltaMovement.z / 2f);
        level.addFreshEntity(projectile);

        setDeltaMovement(deltaMovement.x, deltaMovement.y + 0.5f, deltaMovement.z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ACTIVE_ABILITY_TICKS, -1);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.MATILDA_BIRD.get();
    }
}
