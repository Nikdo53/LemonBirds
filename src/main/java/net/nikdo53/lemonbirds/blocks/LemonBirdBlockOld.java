package net.nikdo53.lemonbirds.blocks;

import dev.ryanhcode.sable.api.block.BlockWithSubLevelCollisionCallback;
import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.nikdo53.lemonbirds.util.LemonUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class LemonBirdBlockOld extends Block implements BlockWithSubLevelCollisionCallback {
    public static final LemonBirdCollisionCallback COLLISION_CALLBACK = new LemonBirdCollisionCallback();

    public LemonBirdBlockOld(Properties properties) {
        super(properties);
    }


    @Override
    public BlockSubLevelCollisionCallback sable$getCallback() {
        return COLLISION_CALLBACK;
    }

   public static class LemonBirdCollisionCallback implements BlockSubLevelCollisionCallback {

        @Override
        public CollisionResult sable$onCollision(BlockPos pos, @Nullable BlockPos otherPos, Vector3d impactPosition, double impactVelocity) {
            if (otherPos == null) return CollisionResult.NONE;

            SubLevelPhysicsSystem system = SubLevelPhysicsSystem.getCurrentlySteppingSystem();
            ServerLevel level = system.getLevel();
            BlockState state = level.getBlockState(pos);

            LevelChunk chunk = LemonUtils.getSubLevelChunk(level, otherPos);

            if (impactVelocity> 0.5) {
                System.out.println("impactVelocity = " + impactVelocity);
            }

            if (impactVelocity > 5) {
                double size = 0.1 * impactVelocity;
                BlockPos.betweenClosedStream(AABB.ofSize(Vec3.atCenterOf(otherPos), size, size, size))
                        .filter(p -> !p.equals(pos)) // dont destroy itself
                        .forEach(blockPos -> level.destroyBlock(blockPos, true));
            }

            return CollisionResult.NONE;
        }
    }

}
