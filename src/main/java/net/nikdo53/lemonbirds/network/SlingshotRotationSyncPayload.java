package net.nikdo53.lemonbirds.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.blocks.BirdSlingshotBlockEntity;
import net.nikdo53.lemonbirds.init.ModDataAttachments;

/**
 * Carries a slingshot's aim out to everyone watching it.
 * <p>
 * The block entity is resynced whole whenever a bird is put in or taken out, but aiming changes every tick a player
 * holds a key, and pushing the entire block entity - plus the block update that comes with it - down the wire that
 * often is far more than three floats are worth.
 */
public record SlingshotRotationSyncPayload(BlockPos pos, float yaw, float pitch, float pull) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SlingshotRotationSyncPayload> TYPE = new CustomPacketPayload.Type<>(LemonBirds.loc("slingshot_rotation_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SlingshotRotationSyncPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SlingshotRotationSyncPayload::pos,
            ByteBufCodecs.FLOAT, SlingshotRotationSyncPayload::yaw,
            ByteBufCodecs.FLOAT, SlingshotRotationSyncPayload::pitch,
            ByteBufCodecs.FLOAT, SlingshotRotationSyncPayload::pull,
            SlingshotRotationSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Level level = player.level();

            // The player driving this slingshot aimed it locally the moment they pressed the key. Their own aim
            // coming back a round trip later would only rubber band it.
            if (pos.equals(player.getExistingDataOrNull(ModDataAttachments.SLINGSHOT))) return;

            if (level.getBlockEntity(pos) instanceof BirdSlingshotBlockEntity blockEntity) {
                blockEntity.updateRotation(yaw, pitch, pull);
            }
        });
    }
}