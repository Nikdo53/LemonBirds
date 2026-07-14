package net.nikdo53.lemonbirds.util;

import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

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


}
