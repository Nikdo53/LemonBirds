package net.nikdo53.lemonbirds.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.blocks.BirdSlingshotBlockEntity;
import net.nikdo53.lemonbirds.init.ModDataAttachments;

public record SlingshotDummyPosPayload(double x, double y, double z) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SlingshotDummyPosPayload> TYPE = new  CustomPacketPayload.Type<>(LemonBirds.loc("slingshot_dummy_pos"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, SlingshotDummyPosPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, SlingshotDummyPosPayload::x,
            ByteBufCodecs.DOUBLE, SlingshotDummyPosPayload::y,
            ByteBufCodecs.DOUBLE, SlingshotDummyPosPayload::z,
            SlingshotDummyPosPayload::new
    );


    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Level level = player.level();

            BlockPos pos = player.getData(ModDataAttachments.SLINGSHOT);
            if (level.getBlockEntity(pos) instanceof BirdSlingshotBlockEntity blockEntity) {
                blockEntity.updateDummyPos(new Vec3(x, y, z));
            }
        });
    }


}
