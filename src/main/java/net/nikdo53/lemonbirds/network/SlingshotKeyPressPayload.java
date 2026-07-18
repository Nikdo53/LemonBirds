package net.nikdo53.lemonbirds.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.blocks.BirdSlingshotBlockEntity;
import net.nikdo53.lemonbirds.entities.AbstractLemonBirdEntity;
import net.nikdo53.lemonbirds.init.ModDataAttachments;

public record SlingshotKeyPressPayload(int key) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SlingshotKeyPressPayload> TYPE = new  CustomPacketPayload.Type<>(LemonBirds.loc("slingshot_key_press"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SlingshotKeyPressPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SlingshotKeyPressPayload::key,
            SlingshotKeyPressPayload::new
    );

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Level level = player.level();

            BlockPos pos = player.getData(ModDataAttachments.SLINGSHOT);
            if (level.getBlockEntity(pos) instanceof BirdSlingshotBlockEntity blockEntity) {
                blockEntity.onKeyPressed(player, key);
            } else {
                player.removeData(ModDataAttachments.SLINGSHOT);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
