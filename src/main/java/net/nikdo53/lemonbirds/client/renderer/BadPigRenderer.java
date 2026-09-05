package net.nikdo53.lemonbirds.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.nikdo53.lemonbirds.blocks.BadPigBlockEntity;
import net.nikdo53.lemonbirds.init.ModBlockTags;
import net.nikdo53.lemonbirds.init.ModClientConfig;
import net.nikdo53.tinymultiblocklib.block.IMultiBlock;

public class BadPigRenderer <T extends BadPigBlockEntity> implements BlockEntityRenderer<T> {
    public BadPigRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (!ModClientConfig.SHOW_PIG_HIGHLIGHTS.get()) return;
        if (player == null) return;
        Item item = player.getMainHandItem().getItem();
        if (!( item instanceof BlockItem blockItem)) return;
        BlockState blockState = blockEntity.getBlockState();
        if (!blockState.is(blockItem.getBlock()) || !IMultiBlock.isCenter(blockState)) return;

        OutlineBufferSource outlineBufferSource = minecraft.renderBuffers().outlineBufferSource();
        if (blockState.is(ModBlockTags.BAD_PIG_BOSS)){
            outlineBufferSource.setColor(255, 207, 0, 100);
        } else {
            outlineBufferSource.setColor(47, 209, 16, 100);
        }
        VertexConsumer vertexConsumer = outlineBufferSource.getBuffer(RenderType.translucent());

        minecraft.getBlockRenderer().renderBatched(blockState, blockEntity.getBlockPos(), blockEntity.getLevel(), poseStack, vertexConsumer, true, blockEntity.getLevel().random);
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
