package net.nikdo53.lemonbirds.init;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public interface ModKeyBinds {
    KeyMapping SLINGSHOT_LAUNCH = new KeyMapping("key.lemonbirds.slingshot_launch", InputConstants.KEY_H, "key.category.lemonbirds.lemonbirds");
    KeyMapping BIRD_ABILITY = new KeyMapping("key.lemonbirds.bird_ability", GLFW.GLFW_KEY_K, "key.category.lemonbirds.lemonbirds");


}
