package me.kalmemarq.jsonui.element.renderer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import me.kalmemarq.jsonui.element.UICustomElement;
import me.kalmemarq.jsonui.util.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class SplashTextRenderer extends DrawableHelper implements ICustomRenderer {
    public static String splashText;
    public float rotation = -20.0f;
    public float speed = 1.0f;
    public Color color = Color.fromRGB(16776960);

    @Override
    public void init(JsonObject obj, UICustomElement element) {
        if (JsonHelper.hasNumber(obj, "rotation")) {
            rotation = JsonHelper.getFloat(obj, "rotation");
        }

        if (JsonHelper.hasNumber(obj, "speed")) {
            speed = JsonHelper.getFloat(obj, "speed");
        }

        if (JsonHelper.hasString(obj, "color")) {
            String c = JsonHelper.getString(obj, "color");
            if (Color.hasName(c)) {
                color = (Color.fromName(c));
            } else {
                color = (Color.fromSRGB(c));
            }
        } else if (JsonHelper.hasArray(obj, "color")) {
            JsonArray arr = JsonHelper.getArray(obj, "color");
            if (arr.size() >= 3 && arr.size() <= 4) {
                color = (Color.fromRGB(arr.get(0).getAsFloat(), arr.get(1).getAsFloat(), arr.get(2).getAsFloat()));
            }
        }
    }

    @Override
    public void render(MinecraftClient client, MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
        if (splashText == null) {
            splashText = client.getSplashTextLoader().get();
        }

        if (splashText != null) {
            matrices.push();
            matrices.translate(90, 70.0D, 0.0D);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(this.rotation));
            float h = 1.8F - MathHelper.abs(MathHelper.sin((float)(Util.getMeasuringTimeMs() % 1000L) / 1000.0F * 6.2831855F * this.speed) * 0.1F);
            h = h * 100.0F / (float)(client.textRenderer.getWidth(splashText) + 32);
            matrices.scale(h, h, h);
            drawCenteredText(matrices, client.textRenderer, splashText, 0, -8, this.color.toColor());
            matrices.pop();
        }
    }
}
