package net.nikdo53.lemonbirds.entities;

import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.level.NoteBlockEvent;
import net.nikdo53.lemonbirds.init.ModEntities;

import java.util.List;

public class YellowLemonBirdEntity extends AbstractLemonBirdEntity{
    public YellowLemonBirdEntity(Level level, Position pos) {
        super(ModEntities.YELLOW_LEMON_BIRD.get(), level, pos);
    }

    public YellowLemonBirdEntity(EntityType<? extends AbstractLemonBirdEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public DestroyEffectivity getDestroyEffectivity() {
        return new DestroyEffectivity(0.8, 0.2, 0.45, 0.75);
    }

    @Override
    protected void activateAbility() {
        Player owner = (Player) getOwner();
        assert owner != null;

        Vec3 deltaMovement = getDeltaMovement();
        this.setDeltaMovement(deltaMovement.add(owner.getLookAngle().scale(1.0)));

    }

    public YellowLemonBirdEntity(Level level, Player player) {
        super(ModEntities.YELLOW_LEMON_BIRD.get(), level, player);
    }

}
