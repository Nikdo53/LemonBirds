package net.nikdo53.lemonbirds.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.entities.AbstractLemonBirdEntity;

public record ActivateLemonBirdPayload(int entityId) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ActivateLemonBirdPayload> TYPE = new  CustomPacketPayload.Type<>(LemonBirds.loc("activate_bird"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ActivateLemonBirdPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ActivateLemonBirdPayload::entityId,
            ActivateLemonBirdPayload::new
    );

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = context.player().level().getEntity(entityId);
            if (entity instanceof AbstractLemonBirdEntity bird){
                bird.onAbilityKey();
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
