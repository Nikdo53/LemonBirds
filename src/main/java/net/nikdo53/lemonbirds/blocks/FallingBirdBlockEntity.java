package net.nikdo53.lemonbirds.blocks;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.nikdo53.lemonbirds.entities.AbstractLemonBirdEntity;
import net.nikdo53.lemonbirds.entities.BombLemonBirdEntity;
import net.nikdo53.lemonbirds.init.ModBlockEntities;
import net.nikdo53.lemonbirds.init.ModBlocks;
import net.nikdo53.lemonbirds.items.BirdItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FallingBirdBlockEntity extends BlockEntity {
    public int tickCount = 0;
    public static final int MAX_TICKS = 200;

    public FallingBirdBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FALLING_BIRD.get(), pos, blockState);
    }

    public void tick(Level level, BlockPos pos, BlockState state){
            tickCount++;
            if ((tickCount >= MAX_TICKS && !level.isClientSide())) {

                if (state.is(ModBlocks.BOMB_BIRD_BLOCK.get())){
                    BombLemonBirdEntity.birdExplosion(level, pos.getCenter(), null);
                }
                
                level.removeBlock(pos, false);
                level.removeBlockEntity(pos);

            }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putInt("tickCount", tickCount);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        tickCount = tag.getInt("tickCount");

    }

}
