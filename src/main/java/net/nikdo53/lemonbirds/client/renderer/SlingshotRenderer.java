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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.nikdo53.lemonbirds.LemonBirds;
import net.nikdo53.lemonbirds.blocks.BirdSlingshotBlock;
import net.nikdo53.lemonbirds.blocks.BirdSlingshotBlockEntity;
import net.nikdo53.lemonbirds.client.model.BirdSlingshotModel;
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
        Direction direction = blockEntity.getBlockState().getValue(BirdSlingshotBlock.FACING).getOpposite();

        poseStack.pushPose();

        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(direction.get2DDataValue() * 90));

        VertexConsumer vertexConsumer = TEXTURE.buffer(bufferSource, RenderType::entityCutout);
        modelBody.render(poseStack, vertexConsumer, packedLight, packedOverlay);

        poseStack.popPose();


        Minecraft minecraft = Minecraft.getInstance();
        boolean isFirstPerson = !minecraft.gameRenderer.getMainCamera().isDetached();
        boolean isInvisible = blockEntity.controllingPlayer == minecraft.player && isFirstPerson;

        poseStack.pushPose();
        poseStack.translate(0.0, 0.5, 0.0);

        Vec3 relative = blockEntity.getRelativeDummyPos(partialTick);
        Vec3 birdPos = blockEntity.getCenterPosition(null).add(relative);
        Vec3 opposite = blockEntity.getLookVec(partialTick).multiply(-1, -1, -1);

        poseStack.translate(birdPos.x(), birdPos.y(), birdPos.z());

        if (direction.getAxis() != Direction.Axis.Z){
            direction = direction.getOpposite();
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(direction.get2DDataValue() * 90));


        poseStack.mulPose(new Quaternionf().rotationXYZ(
                (float) Math.toRadians(opposite.y * 15),
                (float) Math.toRadians(opposite.x * 15),
                0
        ));

        if (blockEntity.hasBirdItem() && !isInvisible){
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

        poseStack.pushPose();
        poseStack.translate(2.0, 0.0, -3);

        poseStack.pushPose();

        poseStack.translate(0.0, 0.0, -relative.length() + 1.8);
        poseStack.scale(1.0F, 1.0F, (float) ( 0.5 + relative.length() / 3.8));
        modelStretch.render(poseStack, vertexConsumer, packedLight, packedOverlay);

        poseStack.popPose();


        modelSupport.render(poseStack, vertexConsumer, packedLight, packedOverlay);

        poseStack.popPose();


        poseStack.popPose();

    }

    @Override
    public AABB getRenderBoundingBox(BirdSlingshotBlockEntity blockEntity) {
        return AABB.of(new BoundingBox(blockEntity.getBlockPos().above(3)).inflatedBy(3));
    }

    int directionToDegrees(Direction direction) {
        return direction.get2DDataValue() * 90;
    }
}
