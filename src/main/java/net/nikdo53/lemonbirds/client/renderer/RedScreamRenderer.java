package net.nikdo53.lemonbirds.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public class RedScreamRenderer<T extends Entity & ItemSupplier> extends EntityRenderer<T> {
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean fullBright;

    public RedScreamRenderer(EntityRendererProvider.Context context, float scale, boolean fullBright) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.scale = scale;
        this.fullBright = fullBright;
    }

    public RedScreamRenderer(EntityRendererProvider.Context context) {
        this(context, 1.5F, false);
    }

    @Override
    protected int getBlockLightLevel(T entity, BlockPos pos) {
        return this.fullBright ? 15 : super.getBlockLightLevel(entity, pos);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (!(entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25)))
            return;

        Vec3 lastPos = entity.getPosition(0);
        Vec3 currentPos = entity.getPosition(1);

        Vec3 difference = currentPos.subtract(lastPos);

        poseStack.pushPose();
            poseStack.translate(0.0D, 0.5D, 0.0D);
            poseStack.scale(this.scale, this.scale, this.scale);

            for (int i = 0; i < 3; i++) {
                if (i > 0 && difference.lengthSqr() < 0.01D) {
                    continue;
                }

                poseStack.pushPose();

                float secDif = i * 2.5f;
                poseStack.translate(difference.x * secDif, difference.y * secDif, difference.z * secDif);

                float sectionScale = 1.0F + (secDif * 0.25F);
                poseStack.scale(sectionScale, sectionScale, sectionScale);

                poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) + 180f));
                poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));

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

                poseStack.popPose();
            }

            poseStack.popPose();

            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

    }

    /**
     * Returns the location of an entity's texture.
     */
    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

}
