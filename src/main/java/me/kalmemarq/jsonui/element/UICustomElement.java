package me.kalmemarq.jsonui.element;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import me.kalmemarq.jsonui.JsonUIMod;
import me.kalmemarq.jsonui.element.renderer.CustomRenderers;
import me.kalmemarq.jsonui.element.renderer.ICustomRenderer;
import me.kalmemarq.jsonui.element.renderer.CustomRenderers.Func;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.JsonHelper;

public class UICustomElement extends UIElement {
    public JsonObject obj;
    @Nullable 
    public ICustomRenderer renderer;

    public UICustomElement() {}

    public void setObj(JsonObject obj) {
        this.obj = obj;
    }

    public void setRenderer(@Nullable ICustomRenderer renderer) {
        this.renderer = renderer;

        if (this.renderer != null) {
            this.renderer.init(obj);
        }
    }

    @Override
    public void render(MinecraftClient client, MatrixStack matrices, int width, int height, int mouseX, int mouseY, float tickDelta) {
        if (this.renderer != null) {
            this.renderer.render(client, matrices, mouseX, mouseY, tickDelta);
        }
    }

    protected static final class Serializer implements IUIElementSerializer {
        @Override
        public UIElement fromJson(JsonObject obj) {
            UICustomElement el = new UICustomElement();

            // bruh
            el.setObj(obj);

            if (JsonHelper.hasString(obj, "renderer")) {
                Func<?> c = CustomRenderers.get(JsonUIMod.getIdentifier(JsonHelper.getString(obj, "renderer")));

                if (c != null) {
                    el.setRenderer(c.create());
                }
            }

            return el;
        }
    }
}
