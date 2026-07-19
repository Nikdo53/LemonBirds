package net.nikdo53.lemonbirds.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.blocks.BirdSlingshotBlock;
import net.nikdo53.lemonbirds.blocks.BirdSlingshotBlockEntity;
import net.nikdo53.lemonbirds.client.model.BirdSlingshotModel;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class SlingshotRenderer implements BlockEntityRenderer<BirdSlingshotBlockEntity> {
    static final Material TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, LemonBirds.loc("block/bird_slingshot"));

    final BlockEntityRendererProvider.Context context;
    final ItemRenderer itemRenderer;
    final ModelPart modelBody;
    final ModelPart modelSupport;
    final ModelPart modelStretch;


    public SlingshotRenderer(BlockEntityRendererProvider.Context context){
        this.context = context;
        this.itemRenderer = context.getItemRenderer();
        this.modelBody = context.bakeLayer(BirdSlingshotModel.BODY_LAYER);
        this.modelSupport = context.bakeLayer(BirdSlingshotModel.SUPPORT_LAYER);
        this.modelStretch = context.bakeLayer(BirdSlingshotModel.STRETCH_LAYER);
    }

    @Override
    public void render(BirdSlingshotBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!blockEntity.isCenter())
            return;

        int time = Math.toIntExact(blockEntity.getLevel().getGameTime() % 360);

        Direction direction = blockEntity.getBlockState().getValue(BirdSlingshotBlock.FACING).getOpposite();
        Vec3i normal = direction.getNormal().multiply(1);
        VertexConsumer vertexConsumer = TEXTURE.buffer(bufferSource, RenderType::entityCutout);

        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);

        int degrees = direction.getAxis() == Direction.Axis.Z ? direction.getOpposite().get2DDataValue() : direction.get2DDataValue();
        poseStack.mulPose(Axis.YP.rotationDegrees(degrees * 90
             //   + time *5
        ));

        Vec3 relative = blockEntity.getRelativeDummyPos(partialTick);
        Vec3 center = blockEntity.getCenterPosition(null);
        Vec3 birdPos = center.add(relative);
        Vec3 lookVec = blockEntity.getLookVec(partialTick);
        Vec3 opposite = lookVec.multiply(-1, -1, -1);

        poseStack.mulPose(new Quaternionf().rotationXYZ(
                0,
                (float) Math.toRadians(opposite.x * 12),
                0
        ));

        renderBlockbenchModel(modelBody, poseStack, vertexConsumer, packedLight, packedOverlay);

        Minecraft minecraft = Minecraft.getInstance();
        boolean isFirstPerson = !minecraft.gameRenderer.getMainCamera().isDetached();
        boolean isInvisible = blockEntity.controllingPlayer == minecraft.player && isFirstPerson;


        poseStack.pushPose();
        {
            poseStack.mulPose(Axis.YP.rotationDegrees(180));

            poseStack.translate(-0.5, -2.0, 1.0);


            poseStack.translate(0.5, birdPos.y(), lookVec.z());

            float centerPoint = 2;
            poseStack.translate(0, 0, centerPoint);

            poseStack.mulPose(new Quaternionf().rotationXYZ(
                    (float) Math.toRadians(opposite.y * 12),
                    (float) Math.toRadians(opposite.x * 2),
                    0
            ));

            poseStack.translate(0, 0, -centerPoint);


            poseStack.pushPose();
            {
                poseStack.scale(2, 2, 2);
                if (blockEntity.hasBirdItem() && !isInvisible) {
                    itemRenderer.renderStatic(blockEntity.birdItem.getDefaultInstance(),
                            ItemDisplayContext.GROUND,
                            packedLight,
                            packedOverlay,
                            poseStack,
                            bufferSource,
                            blockEntity.getLevel(),
                            53
                    );
                }
            }
            poseStack.popPose();

            poseStack.pushPose();
            {
                poseStack.translate(-0.0, -3.8, -3);
                renderBlockbenchModel(modelSupport, poseStack, vertexConsumer, packedLight, packedOverlay);


                double distance = (lookVec.z + Math.abs(lookVec.y)) / 2;
                double scale = (distance / 2) + 0.1;

                poseStack.translate(0.0, 0.0,   -scale * 4 + 2.75 );
                poseStack.scale(1.0F, 1.0F, (float) (scale + 0.3));
                renderBlockbenchModel(modelStretch, poseStack, vertexConsumer, packedLight, packedOverlay);

            }
            poseStack.popPose();


        }
        poseStack.popPose();

        poseStack.popPose();

    }

    @Override
    public @NotNull AABB getRenderBoundingBox(BirdSlingshotBlockEntity blockEntity) {
        return AABB.of(new BoundingBox(blockEntity.getBlockPos().above(3)).inflatedBy(3));
    }


    public static void renderBlockbenchModel(ModelPart modelPart, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        modelPart.render(poseStack, vertexConsumer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
