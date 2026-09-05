package net.nikdo53.lemonbirds.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.nikdo53.lemonbirds.blocks.BombFallingBirdBlockEntity;
import net.nikdo53.lemonbirds.blocks.FallingBirdBlock;
import net.nikdo53.lemonbirds.entities.BombLemonBirdEntity;
import net.nikdo53.lemonbirds.init.ModItems;
import net.nikdo53.nikdocolor.NikdoColor;
import net.nikdo53.tinymultiblocklib.client.TintedBufferSource;

public class BombBlockRenderer<T extends BombFallingBirdBlockEntity> implements BlockEntityRenderer<T> {
    public BombBlockRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BombFallingBirdBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        if (!entity.getBlockState().getValue(FallingBirdBlock.DESPAWNS)){
            return;
        }

        float explodingTicks = entity.tickCount + partialTicks;
        float explosionProgress =  explodingTicks / BombLemonBirdEntity.EXPLOSION_DELAY;
        float explosionScale = 1f + explosionProgress * 2f;

        NikdoColor.RGB color = NikdoColor.fromHex(0x00FFFFFF);
        NikdoColor.RGB orange = NikdoColor.fromHex(0xffFFFFFF);
        color.operation(orange, (a, b) -> Mth.lerp(explosionProgress, a, b));

        poseStack.pushPose();

        poseStack.scale(explosionScale, explosionScale, explosionScale);

        itemRenderer
                .renderStatic(
                        ModItems.BOMB_LEMON.toStack(),
                        ItemDisplayContext.GROUND,
                        packedLight,
                        OverlayTexture.NO_OVERLAY,
                        poseStack,
                        buffer,
                        entity.getLevel(),
                        53
                );

        poseStack.scale(1.01f, 1.01f, 1.01f);
        itemRenderer
                .renderStatic(
                        ModItems.BOMB_LEMON_ORANGE.get().getDefaultInstance(),
                        ItemDisplayContext.GROUND,
                        packedLight,
                        OverlayTexture.NO_OVERLAY,
                        poseStack,
                        buffer instanceof MultiBufferSource.BufferSource source ? new TintedBufferSource(source, color) : buffer,
                        entity.getLevel(),
                        53
                );

        poseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(T blockEntity) {
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity).inflate(2);
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
