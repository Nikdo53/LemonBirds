package net.nikdo53.lemonbirds.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.entities.AbstractLemonBirdEntity;

public record ActivateLemonBirdPayload(int entityId, float xRot, float yRot) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ActivateLemonBirdPayload> TYPE = new  CustomPacketPayload.Type<>(LemonBirds.loc("activate_bird"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ActivateLemonBirdPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ActivateLemonBirdPayload::entityId,
            ByteBufCodecs.FLOAT, ActivateLemonBirdPayload::xRot,
            ByteBufCodecs.FLOAT, ActivateLemonBirdPayload::yRot,
            ActivateLemonBirdPayload::new
    );

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = context.player().level().getEntity(entityId);
            if (entity instanceof AbstractLemonBirdEntity bird){
                bird.onAbilityKey(xRot, yRot);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
