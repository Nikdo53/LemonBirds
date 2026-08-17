package net.nikdo53.lemonbirds.client.model;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.nikdo53.lemonbirds.LemonBirds;

public class BirdSlingshotModel {
	public static final ModelLayerLocation BODY_LAYER = new ModelLayerLocation(LemonBirds.loc("bird_slingshot"), "main");
	public static final ModelLayerLocation SUPPORT_LAYER = new ModelLayerLocation(LemonBirds.loc("bird_slingshot"), "support");
	public static final ModelLayerLocation STRETCH_LAYER = new ModelLayerLocation(LemonBirds.loc("bird_slingshot"), "stretch");

	public static LayerDefinition bodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone7 = partdefinition.addOrReplaceChild("bone7", CubeListBuilder.create().texOffs(72, 0).addBox(-11.0F, -4.0F, 39.0F, 22.0F, 4.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, -50.0F));

		PartDefinition bone = bone7.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(50, 154).addBox(-7.0F, -17.0F, 43.0F, 14.0F, 13.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(156, 158).addBox(-6.0F, -39.0F, 44.0F, 12.0F, 22.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(160, 0).addBox(23.7695F, -107.2843F, 43.0F, 14.0F, 6.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(0, 173).addBox(-36.7696F, -87.2843F, 44.0F, 12.0F, 20.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(176, 88).addBox(-37.7696F, -107.2843F, 43.0F, 14.0F, 6.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(176, 56).addBox(24.7695F, -87.2843F, 44.0F, 12.0F, 20.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(120, 130).mirror().addBox(-7.0F, -7.0F, 43.0F, 14.0F, 14.0F, 14.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(120, 84).addBox(-7.0F, -46.0F, 43.0F, 14.0F, 14.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -39.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition cube_r2 = bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(72, 90).addBox(-12.0F, -7.0F, -6.0F, 12.0F, 52.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(31.8198F, -62.3345F, 50.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition cube_r3 = bone.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(120, 56).addBox(-7.0F, -46.0F, 43.0F, 14.0F, 14.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -39.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition cube_r4 = bone.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(72, 26).addBox(0.0F, -7.0F, -6.0F, 12.0F, 52.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-31.8198F, -62.3345F, 50.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition bone8 = bone.addOrReplaceChild("bone8", CubeListBuilder.create().texOffs(120, 26).addBox(53.5391F, -7.0F, -16.0F, 16.0F, 14.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(0, 92).addBox(-8.0F, -7.0F, -16.0F, 16.0F, 14.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(-30.7695F, -94.2843F, 58.0F));

		PartDefinition bone3 = bone8.addOrReplaceChild("bone3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bone6 = bone3.addOrReplaceChild("bone6", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bone5 = bone3.addOrReplaceChild("bone5", CubeListBuilder.create(), PartPose.offset(61.5391F, 0.0F, 0.0F));

		PartDefinition bone4 = bone3.addOrReplaceChild("bone4", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	public static LayerDefinition supportLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition support = partdefinition.addOrReplaceChild("support", CubeListBuilder.create().texOffs(120, 112).addBox(2.0F, -7.0F, 47.0F, 29.0F, 14.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 122).addBox(30.5391F, -7.0F, 47.0F, 29.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-30.7695F, -70.2843F, 8.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	public static LayerDefinition stretchLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition stretch = partdefinition.addOrReplaceChild("stretch", CubeListBuilder.create(), PartPose.offset(-30.7695F, -70.2843F, 8.0F));

		PartDefinition bone6 = stretch.addOrReplaceChild("bone6", CubeListBuilder.create().texOffs(0, 140).addBox(-3.0F, -7.0F, 0.0F, 6.0F, 14.0F, 19.0F, new CubeDeformation(0.0F))
				.texOffs(0, 46).addBox(-2.0F, -7.0F, 19.0F, 4.0F, 14.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bone5 = stretch.addOrReplaceChild("bone5", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -7.0F, 19.0F, 4.0F, 14.0F, 32.0F, new CubeDeformation(0.0F))
				.texOffs(106, 158).addBox(-3.0F, -7.0F, 0.0F, 6.0F, 14.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offset(61.5391F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}


}