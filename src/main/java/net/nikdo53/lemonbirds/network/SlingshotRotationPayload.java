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

public record SlingshotRotationPayload(float yaw, float pitch, float pull) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SlingshotRotationPayload> TYPE = new  CustomPacketPayload.Type<>(LemonBirds.loc("slingshot_rotation"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, SlingshotRotationPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, SlingshotRotationPayload::yaw,
            ByteBufCodecs.FLOAT, SlingshotRotationPayload::pitch,
            ByteBufCodecs.FLOAT, SlingshotRotationPayload::pull,
            SlingshotRotationPayload::new
    );


    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Level level = player.level();

            BlockPos pos = player.getData(ModDataAttachments.SLINGSHOT);
            if (level.getBlockEntity(pos) instanceof BirdSlingshotBlockEntity blockEntity) {
                blockEntity.updateRotation(yaw, pitch, pull);
            }
        });
    }


}
