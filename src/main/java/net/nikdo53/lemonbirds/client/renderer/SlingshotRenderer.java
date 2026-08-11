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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.blocks.BirdSlingshotBlock;
import net.nikdo53.lemonbirds.blocks.BirdSlingshotBlockEntity;
import net.nikdo53.lemonbirds.client.model.BirdSlingshotModel;
import net.nikdo53.lemonbirds.init.ModDataAttachments;
import org.jetbrains.annotations.NotNull;

public class SlingshotRenderer implements BlockEntityRenderer<BirdSlingshotBlockEntity> {
    static final Material TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, LemonBirds.loc("block/bird_slingshot"));

    /** Height the model is drawn at, so that its own offsets land the base plate on the bottom of the block. */
    static final double MODEL_ORIGIN_Y = 1.5;
    /** The fork tips, in model space. Sits on the axis the frame turns around, so the bands stay put while it swings. */
    static final double ANCHOR_Y = BirdSlingshotBlockEntity.ANCHOR_HEIGHT - MODEL_ORIGIN_Y;
    /** How far in front of the pouch the bird sits, so it rests against the cradle instead of inside it. */
    static final double BIRD_INSET = 0.5;

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

        Direction direction = blockEntity.getBlockState().getValue(BirdSlingshotBlock.FACING).getOpposite();
        VertexConsumer vertexConsumer = TEXTURE.buffer(bufferSource, RenderType::entityCutout);

        float yaw = blockEntity.getYaw(partialTick);
        float pitch = blockEntity.getPitch(partialTick);
        double pouchDistance = blockEntity.getPouchDistance(partialTick);

        poseStack.pushPose();
        poseStack.translate(0.5, MODEL_ORIGIN_Y, 0.5);

        int degrees = direction.getAxis() == Direction.Axis.Z ? direction.getOpposite().get2DDataValue() : direction.get2DDataValue();
        poseStack.mulPose(Axis.YP.rotationDegrees(degrees * 90));

        // the whole frame swings around to follow the aim, taking the fork tips - and so the bands - with it
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        renderBlockbenchModel(modelBody, poseStack, vertexConsumer, packedLight, packedOverlay);

        poseStack.pushPose();
        {
            // hang everything below off the fork tips, tilted along the aim, with +Z running back down the bands
            poseStack.translate(0, ANCHOR_Y, 0);
            poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            poseStack.translate(0, -ANCHOR_Y, 0);

            // the bands are modelled reaching from the fork tips to the resting pouch, so stretching them along Z
            // from where they leave the fork keeps both ends attached however far the pouch has been dragged back
            float stretch = (float) ((pouchDistance - BirdSlingshotBlockEntity.BAND_START) / BirdSlingshotBlockEntity.BAND_REST_LENGTH);

            poseStack.pushPose();
            {
                poseStack.translate(0, 0, BirdSlingshotBlockEntity.BAND_START * (1 - stretch));
                poseStack.scale(1.0F, 1.0F, stretch);
                renderBlockbenchModel(modelStretch, poseStack, vertexConsumer, packedLight, packedOverlay);
            }
            poseStack.popPose();

            // the cradle rides the far end of the bands
            poseStack.pushPose();
            {
                poseStack.translate(0, 0, pouchDistance - BirdSlingshotBlockEntity.POUCH_REST_DISTANCE);
                renderBlockbenchModel(modelSupport, poseStack, vertexConsumer, packedLight, packedOverlay);
            }
            poseStack.popPose();

            // the bird sits where the camera is, so drop it while its owner is looking through it
            Minecraft minecraft = Minecraft.getInstance();
            boolean isFirstPerson = !minecraft.gameRenderer.getMainCamera().isDetached();
            BlockPos playerSlingshotPos = minecraft.player.getExistingDataOrNull(ModDataAttachments.SLINGSHOT);
            boolean isInvisible = playerSlingshotPos != null && playerSlingshotPos.equals(blockEntity.getBlockPos()) && isFirstPerson;

            if (blockEntity.hasBirdItem() && !isInvisible) {
                poseStack.pushPose();
                {
                    // the parts bring the anchor height along in their own offsets, an item has to be lifted to it
                    poseStack.translate(0, ANCHOR_Y, pouchDistance - BIRD_INSET);
                    Vec3 slingshotModelOffset = blockEntity.birdItem.getSlingshotModelOffset();
                    poseStack.translate(slingshotModelOffset.x(), slingshotModelOffset.y(), slingshotModelOffset.z());
                    poseStack.scale(2, 2, 2);
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
                poseStack.popPose();
            }
        }
        poseStack.popPose();

        poseStack.popPose();

    }

    @Override
    public @NotNull AABB getRenderBoundingBox(BirdSlingshotBlockEntity blockEntity) {
        // the pouch can swing a long way behind the frame, so cover everything it can reach
        double reach = BirdSlingshotBlockEntity.POUCH_REST_DISTANCE + BirdSlingshotBlockEntity.MAX_PULL;
        return AABB.ofSize(blockEntity.getAnchorPosition(blockEntity.getBlockPos()), reach * 2, reach * 2, reach * 2);
    }


    public static void renderBlockbenchModel(ModelPart modelPart, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        modelPart.render(poseStack, vertexConsumer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
