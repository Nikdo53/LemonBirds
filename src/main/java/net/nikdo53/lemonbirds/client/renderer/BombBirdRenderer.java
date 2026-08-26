package net.nikdo53.lemonbirds.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemDisplayContext;
import net.nikdo53.lemonbirds.entities.AbstractLemonBirdEntity;
import net.nikdo53.lemonbirds.entities.BombLemonBirdEntity;
import net.nikdo53.lemonbirds.init.ModItems;
import net.nikdo53.nikdocolor.NikdoColor;
import net.nikdo53.tinymultiblocklib.client.TintedBufferSource;

public class BombBirdRenderer<T extends BombLemonBirdEntity & ItemSupplier> extends EntityRenderer<T> {
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean fullBright;

    public BombBirdRenderer(EntityRendererProvider.Context context, float scale, boolean fullBright) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.scale = scale;
        this.fullBright = fullBright;
    }

    public BombBirdRenderer(EntityRendererProvider.Context context) {
        this(context, 2.0F, false);
    }

    @Override
    protected int getBlockLightLevel(T entity, BlockPos pos) {
        return this.fullBright ? 15 : super.getBlockLightLevel(entity, pos);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25)) {
            if (entity.isControllingPlayer(Minecraft.getInstance().player) && Minecraft.getInstance().options.getCameraType().isFirstPerson())
                return;

            float explodingTicks = entity.getExplodingTicks() + partialTicks;
            float explosionProgress = entity.isExploding() ? explodingTicks / BombLemonBirdEntity.EXPLOSION_DELAY : 0;
            float explosionScale = 1f + explosionProgress * 2f;
            NikdoColor.RGB color = NikdoColor.fromHex(0x00FFFFFF);
            NikdoColor.RGB orange = NikdoColor.fromHex(0xffFFFFFF);
            color.operation(orange, (a, b) -> Mth.lerp(explosionProgress, a, b));

            poseStack.pushPose();

            poseStack.scale(this.scale, this.scale, this.scale);
            poseStack.scale(explosionScale, explosionScale, explosionScale);


            poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getViewYRot(partialTicks) - 180F));
            poseStack.mulPose(Axis.XP.rotationDegrees(-entity.getViewXRot(partialTicks)));

            this.itemRenderer
                    .renderStatic(
                            entity.getItem(),
                            ItemDisplayContext.GROUND,
                            packedLight,
                            OverlayTexture.NO_OVERLAY,
                            poseStack,
                            buffer,
                            entity.level(),
                            entity.getId()
                    );

            poseStack.scale(1.01f, 1.01f, 1.01f);
            this.itemRenderer
                    .renderStatic(
                            ModItems.BOMB_LEMON_ORANGE.get().getDefaultInstance(),
                            ItemDisplayContext.GROUND,
                            packedLight,
                            OverlayTexture.NO_OVERLAY,
                            poseStack,
                            buffer instanceof MultiBufferSource.BufferSource source ? new TintedBufferSource(source, color) : buffer,
                            entity.level(),
                            entity.getId()
                    );

            poseStack.popPose();
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }
    }

    /**
     * Returns the location of an entity's texture.
     */
    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
