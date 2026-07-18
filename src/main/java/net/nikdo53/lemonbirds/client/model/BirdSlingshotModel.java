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

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create().texOffs(144, 136).addBox(-9.0F, -32.0F, -8.0F, 18.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(0, 166).addBox(-9.0F, -48.0F, -8.0F, 18.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(144, 168).addBox(-8.0F, -64.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(208, 168).addBox(20.0F, -68.0F, -8.0F, 4.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(64, 202).addBox(24.0F, -64.0F, -8.0F, 14.0F, 14.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(196, 60).addBox(24.0F, -80.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(72, 136).addBox(23.0F, -112.0F, -9.0F, 18.0F, 16.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(68, 170).addBox(-24.0F, -64.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(212, 124).addBox(-24.0F, -68.0F, -8.0F, 4.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(0, 132).addBox(-41.0F, -112.0F, -9.0F, 18.0F, 16.0F, 18.0F, new CubeDeformation(0.0F))
				.texOffs(204, 20).addBox(-38.0F, -64.0F, -8.0F, 14.0F, 14.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(196, 92).addBox(-40.0F, -80.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(0, 198).addBox(-40.0F, -96.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(196, 200).addBox(8.0F, -64.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(132, 200).addBox(24.0F, -96.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(108, 20).addBox(-12.0F, -16.0F, -12.0F, 24.0F, 16.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 512, 512);
	}

	public static LayerDefinition supportLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition support = partdefinition.addOrReplaceChild("support", CubeListBuilder.create().texOffs(108, 0).addBox(2.0F, -8.0F, 46.0F, 60.0F, 16.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-64.0F, -8.0F, 11.0F));

		return LayerDefinition.create(meshdefinition, 512, 512);
	}

	public static LayerDefinition stretchLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone6 = partdefinition.addOrReplaceChild("bone6", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -8.0F, 0.0F, 4.0F, 16.0F, 50.0F, new CubeDeformation(0.0F))
				.texOffs(0, 66).addBox(-66.0F, -8.0F, 0.0F, 4.0F, 16.0F, 50.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 11.0F));

		return LayerDefinition.create(meshdefinition, 512, 512);
	}


}