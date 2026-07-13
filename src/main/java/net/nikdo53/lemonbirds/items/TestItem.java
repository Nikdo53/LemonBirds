package net.nikdo53.lemonbirds.items;

import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;

public class TestItem extends Item {
    public TestItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());

        if (SableCompanion.INSTANCE.getContaining(level, pos) instanceof ServerSubLevel subLevel) {
            ServerLevelPlot plot = subLevel.getPlot();
            ChunkPos global = new ChunkPos(pos);
            LevelChunk chunk = plot.getChunk(plot.toLocal(global));
            if (chunk != null){
                chunk.setBlockState(pos, Blocks.DIAMOND_BLOCK.defaultBlockState(), false);
            } else {
                System.out.println("FUCK YOUUU!!!!1!1!1!");
            }
        }
        return InteractionResult.SUCCESS;
    }
}
