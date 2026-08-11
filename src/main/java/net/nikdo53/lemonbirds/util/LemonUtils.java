package net.nikdo53.lemonbirds.util;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.blocks.FallingBirdBlock;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;

import java.util.List;
import java.util.Set;


public class LemonUtils {
    public static LevelChunk getSubLevelChunk(Level level, BlockPos pos) {
        SubLevel subLevel = (SubLevel) SableCompanion.INSTANCE.getContaining(level, pos);

        ChunkPos global = new ChunkPos(pos);
        if (subLevel != null) {
            LevelPlot plot = subLevel.getPlot();
            return plot.getChunk(plot.toLocal(global));
        }

        return level.getChunk(global.x, global.z);
    }

    public static void destroyBlock(BlockPos pos, LevelChunk chunk) {
        BlockState state = chunk.getBlockState(pos);
        if (state.isAir()) return;

        chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);

    }


    public static void assembleIntoSubLevel(ServerLevel level, Block multiBlock, BlockPos pos,
                                            Set<BlockPos> shape, @Nullable String name, @Nullable Vec2 rotationVector, @Nullable Vec3 bounce) {
        if (!level.getBlockState(pos).is(multiBlock)) {
            return;
        }

        // Only hand sable the positions the multiblock actually occupies. Anything else is air, which has no mass, and a
        // sub-level without mass is thrown straight back out by sable in the same tick it's created.
        List<BlockPos> blocks = shape.stream()
                .filter(blockPos -> level.getBlockState(blockPos).is(multiBlock))
                .toList();

        final BoundingBox3i bounds = BoundingBox3i.from(blocks);
        if (bounds == null) {
            LemonBirds.LOGGER.error("Failed to create bounding box for falling bird block at {}", pos);
            return;
        }

        bounds.set(
                bounds.minX - 1,
                bounds.minY - 1,
                bounds.minZ - 1,
                bounds.maxX + 1,
                bounds.maxY + 1,
                bounds.maxZ + 1
        );

        ServerSubLevel subLevel;
        try {
            subLevel = SubLevelAssemblyHelper.assembleBlocks(level, pos, blocks, bounds);
        } catch (RuntimeException e) {
            LemonBirds.LOGGER.error("Unable to create sub-level for falling bird block at {}", pos, e);
            return;
        }

        if (name != null) {
            subLevel.setName(name);
        }

        if (subLevel.isRemoved() || subLevel.getMassTracker().isInvalid()) {
            LemonBirds.LOGGER.error("Sub-level for falling bird block at {} has no mass, assembled {} block(s)",
                    pos, blocks.size());
            return;
        }

        if (rotationVector == null || bounce == null) {
            return;
        }

        SubLevelPhysicsSystem system = SubLevelPhysicsSystem.get(level);
        if (system == null) return;

        Pose3d pose = subLevel.logicalPose();

        Quaterniond orientation = new Quaterniond();
        orientation.rotateY(-Math.toRadians(rotationVector.y));
        orientation.rotateX(Math.toRadians(rotationVector.x));

        pose.orientation().set(orientation);
        system.getPipeline().teleport(subLevel, pose.position(), pose.orientation());

        LemonUtils.applyPhysics(level, subLevel, bounce, 10);
    }

    public static void applyPhysics(ServerLevel level, ServerSubLevel subLevel, Vec3 normal, double scale) {
        SubLevelPhysicsSystem system = SubLevelPhysicsSystem.get(level);

        if (system != null && subLevel != null) {
            Vec3 movement = normal.scale(scale);
            // movement = subLevel.logicalPose().transformNormalInverse(movement);

            RigidBodyHandle handle = RigidBodyHandle.of(subLevel);
            if (handle != null && handle.isValid()) {
                handle.applyLinearImpulse(JOMLConversion.toJOML(movement));
            }
        }
    }

    public static @Nullable ServerSubLevel createOrGetSubLevel(ServerLevel level, BlockPos pos) {
        ServerSubLevel subLevel = (ServerSubLevel) SableCompanion.INSTANCE.getContaining(level, pos);

        if (subLevel == null) {


            int size = 2;
            final BoundingBox boundingBox = BoundingBox.fromCorners(pos.offset(size, size, size), pos.offset(-size, -size, -size));

            final List<BlockPos> blocks = BlockPos.betweenClosedStream(boundingBox).map(BlockPos::immutable).toList();
            final BlockPos anchor = blocks.getFirst();

            final BoundingBox3i bounds = new BoundingBox3i(boundingBox);
            bounds.set(
                    bounds.minX - 1,
                    bounds.minY - 1,
                    bounds.minZ - 1,
                    bounds.maxX + 1,
                    bounds.maxY + 1,
                    bounds.maxZ + 1
            );

            subLevel = SubLevelAssemblyHelper.assembleBlocks(level, anchor, blocks, bounds);

        }

        return subLevel;
    }




}
