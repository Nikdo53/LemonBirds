package net.nikdo53.lemonbirds.init;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.nikdo53.lemonbirds.LemonBirds;

import java.util.UUID;
import java.util.function.Supplier;

public interface ModDataAttachments {

    DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(
            NeoForgeRegistries.ATTACHMENT_TYPES, LemonBirds.MOD_ID);


    Supplier<AttachmentType<Integer>> LEMON_BIRD = ATTACHMENT_TYPES.register(
            "lemon_bird", () -> AttachmentType.builder(() -> -1)
                    .sync(ByteBufCodecs.INT)
                    .build()
    );

}
