package me.kalmemarq.jsonui.element;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Map;

public abstract class UIElement {
    public PropertyBag bag = new PropertyBag();

    public void setPropertyBag(PropertyBag bag) {
        for (Map.Entry<String, PropertyBag.Property> e : bag.getProperties().entrySet()) {
            this.bag.addProperty(e.getKey(), e.getValue().getAsString());
        }
    }

    public PropertyBag getPropertyBag() {
        return this.bag;
    }

    public void render(MinecraftClient client, MatrixStack matrices, int width, int height, int mouseX, int mouseY, float tickDelta) {
    }
}
