package net.nikdo53.lemonbirds.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.Block;
import net.nikdo53.lemonbirds.blocks.BadPigBlock;

import java.util.Map;

public class PigOverlay implements LayeredDraw.Layer {
    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) return;

        int uiX = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int uiY = Minecraft.getInstance().getWindow().getGuiScaledHeight();


        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        Map<Block, Integer> map = BadPigBlock.getAttachment(level);
        int i = 0;
        for (Block block : map.keySet()) {
            Integer count = map.get(block);
            if (count == 0) continue;
            guiGraphics.drawString(minecraft.font, block.getName().append(":").append(String.valueOf(count)),  uiX / 2, uiY / 2 + i * 10, 0xFFFFFF);

            i++;
        }


        poseStack.popPose();

    }
}
