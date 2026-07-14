package net.nikdo53.lemonbirds.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.nikdo53.lemonbirds.blocks.FallingBirdBlockEntity;
import net.nikdo53.lemonbirds.entities.AbstractLemonBirdEntity;
import net.nikdo53.lemonbirds.init.ModItems;

public class FallingBirdBlockRenderer implements BlockEntityRenderer<FallingBirdBlockEntity> {
    final BlockEntityRendererProvider.Context context;
    public FallingBirdBlockRenderer(BlockEntityRendererProvider.Context context){
        this.context = context;
    }

    @Override
    public void render(FallingBirdBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockPos pos = blockEntity.getBlockPos();
        poseStack.pushPose();
        poseStack.scale(1, 1, 1);

        if (blockEntity.hasBird()) {
            Vec3 birdPos = blockEntity.birdPos;
            Vec3 offsets = birdPos.subtract(Vec3.atLowerCornerOf(pos));
            poseStack.translate(offsets.x, Math.min(0.5, offsets.y), offsets.z);

            context.getItemRenderer()
                    .renderStatic(
                            blockEntity.birdItem,
                            ItemDisplayContext.GROUND,
                            packedLight,
                            OverlayTexture.NO_OVERLAY,
                            poseStack,
                            bufferSource,
                            blockEntity.getLevel(),
                            53
                    );
        }

        poseStack.popPose();
    }
}
