package net.nikdo53.lemonbirds.entities;

import net.minecraft.core.Position;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.nikdo53.lemonbirds.init.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BombLemonBirdEntity extends AbstractLemonBirdEntity{
    private static final EntityDataAccessor<Integer> EXPLODING_TICKS = SynchedEntityData.defineId(
            BombLemonBirdEntity.class, EntityDataSerializers.INT
    );

    public static final int EXPLOSION_DELAY = 20;

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
    protected void activateAbility(float xRot, float yRot) {
        this.getEntityData().set(EXPLODING_TICKS, 0);
    }

    @Override
    public void turnIntoBlock() {
        if (hasAbility()) {
            super.turnIntoBlock();
        } else if (!isRemoved()){
            birdExplosion(level(), this.position(), this);
        }
    }

    public int getExplodingTicks() {
        return this.getEntityData().get(EXPLODING_TICKS);
    }

    public boolean isExploding() {
        return this.getExplodingTicks() >= 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (getExplodingTicks() >= EXPLOSION_DELAY && !isRemoved()){
            birdExplosion(level(), this.position(), this);
            this.discard();
        } else if (isExploding()){
            this.getEntityData().set(EXPLODING_TICKS, getExplodingTicks() + 1);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(EXPLODING_TICKS, -1);
    }

    public static void birdExplosion(Level level, Position pos, @Nullable Entity source){
        level.explode(source, pos.x(), pos.y(), pos.z(), 4.0f, Level.ExplosionInteraction.MOB);
    }

    public BombLemonBirdEntity(Level level, Player player) {
        super(ModEntities.BOMB_LEMON_BIRD.get(), level, player);
    }

}
