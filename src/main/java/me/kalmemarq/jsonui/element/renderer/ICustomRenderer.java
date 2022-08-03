package me.kalmemarq.jsonui.element.renderer;

import com.google.gson.JsonObject;

import me.kalmemarq.jsonui.element.UICustomElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public interface ICustomRenderer {
    void init(JsonObject obj, UICustomElement element);
    void render(MinecraftClient client, MatrixStack matrices, int mouseX, int mouseY, float tickDelta);
}
