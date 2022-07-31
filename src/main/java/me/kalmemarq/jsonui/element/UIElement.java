package me.kalmemarq.jsonui.element;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public abstract class UIElement {
    public void render(MinecraftClient client, MatrixStack matrices, int width, int height, int mouseX, int mouseY, float tickDelta) {
    }
}
