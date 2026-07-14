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
import net.nikdo53.lemonbirds.init.ModBlockEntities;
import net.nikdo53.lemonbirds.items.BirdItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FallingBirdBlockEntity extends BlockEntity {
    public int tickCount = 0;
    public static final int MAX_TICKS = 200;

    public Vec3 birdPos;
    public ItemStack birdItem;

    public FallingBirdBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FALLING_BIRD.get(), pos, blockState);
    }

    public FallingBirdBlockEntity(BlockPos pos, BlockState blockState, Vec3 birdPos, ItemStack birdItem) {
        super(ModBlockEntities.FALLING_BIRD.get(), pos, blockState);
        this.birdPos = birdPos;
        this.birdItem = birdItem;

        setChanged();
    }

    public boolean hasBird() {
        return birdItem != null && birdPos != null;
    }

    public void tick(Level level, BlockPos pos, BlockState state){
            tickCount++;
            if ((tickCount >= MAX_TICKS || !hasBird()) && !level.isClientSide) {
                if (hasBird()) {
                    ((BirdItem) birdItem.getItem()).asProjectile(level, birdPos, Direction.NORTH).onBlockEntityDespawn(pos, level);
                }

                level.removeBlockEntity(pos);
                level.removeBlock(pos, false);
            }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag updateTag = super.getUpdateTag(registries);
        saveAdditional(updateTag, registries);
        return updateTag;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (hasBird()) {
            tag.putDouble("birdX", birdPos.x);
            tag.putDouble("birdY", birdPos.y);
            tag.putDouble("birdZ", birdPos.z);

            tag.put("birdItem", birdItem.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        this.birdPos = new Vec3(tag.getDouble("birdX"), tag.getDouble("birdY"), tag.getDouble("birdZ"));
        this.birdItem = ItemStack.parse(registries, tag.getCompound("birdItem")).orElseThrow();
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
