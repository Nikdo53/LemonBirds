package net.nikdo53.lemonbirds.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.blocks.BadPigBlock;

import java.util.HashMap;
import java.util.Map;
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

    Supplier<AttachmentType<BlockPos>> SLINGSHOT = ATTACHMENT_TYPES.register(
            "slingshot", () -> AttachmentType.<BlockPos>builder(() -> null)
                    .serialize(BlockPos.CODEC)
                    .sync(BlockPos.STREAM_CODEC)
                    .build()
    );

    Supplier<AttachmentType<Map<Block, Integer>>> PIGS_REMAINING = ATTACHMENT_TYPES.register(
            "pigs_remaining", () -> AttachmentType.builder(Map::<Block, Integer>of)
                    .sync(BadPigBlock.PIG_STREAM_CODEC)
                    .serialize(BadPigBlock.PIG_CODEC)
                    .build()
    );



}
