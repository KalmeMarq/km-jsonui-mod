package me.kalmemarq.jsonui.element.renderer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;

import me.kalmemarq.jsonui.element.UICustomElement;
import me.kalmemarq.jsonui.util.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Matrix4f;

public class GradientRenderer implements ICustomRenderer {
    public GradientDirection direction = GradientDirection.VERTICAL;
    public Color colorStart = Color.WHITE;
    public Color colorEnd = Color.WHITE;

    public GradientRenderer() {
    }

    public void init(JsonObject obj, UICustomElement element) {
        if (JsonHelper.hasString(obj, "color_start")) {
            String c = JsonHelper.getString(obj, "color_start");
            if (Color.hasName(c)) {
                colorStart = (Color.fromName(c));
            } else {
                colorStart = (Color.fromSRGB(c));
            }
        } else if (JsonHelper.hasArray(obj, "color_start")) {
            JsonArray arr = JsonHelper.getArray(obj, "color_start");
            if (arr.size() >= 3 && arr.size() <= 4) {
                colorStart = (Color.fromRGB(arr.get(0).getAsFloat(), arr.get(1).getAsFloat(), arr.get(2).getAsFloat()));
            }
        }

        if (JsonHelper.hasString(obj, "color_end")) {
            String c = JsonHelper.getString(obj, "color_end");
            if (Color.hasName(c)) {
                colorEnd = (Color.fromName(c));
            } else {
                colorEnd = (Color.fromSRGB(c));
            }
        } else if (JsonHelper.hasArray(obj, "color_end")) {
            JsonArray arr = JsonHelper.getArray(obj, "color_end");
            if (arr.size() >= 3 && arr.size() <= 4) {
                colorEnd = (Color.fromRGB(arr.get(0).getAsFloat(), arr.get(1).getAsFloat(), arr.get(2).getAsFloat()));
            }
        }

        if (JsonHelper.hasString(obj, "gradient_direction")) {
            direction = JsonHelper.getString(obj, "gradient_direction").equals("horizontal") ? GradientDirection.HORIZONTAl : GradientDirection.VERTICAL; 
        }
    }

    public void render(MinecraftClient client, MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
        float rS = this.colorStart.r;
        float gS = this.colorStart.g;
        float bS = this.colorStart.b;
        float aS = this.colorStart.a;

        float rE = this.colorEnd.r;
        float gE = this.colorEnd.g;
        float bE = this.colorEnd.b;
        float aE = this.colorEnd.a;

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        int startX = 0;
        int startY = 100;
        int endX = 100;
        int endY = 200;
        int z = 0;

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        if (direction == GradientDirection.VERTICAL) {
            bufferBuilder.vertex(matrix, (float)endX, (float)startY, (float)z).color(rS, gS, bS, aS).next();
            bufferBuilder.vertex(matrix, (float)startX, (float)startY, (float)z).color(rS, gS, bS, aS).next();
            bufferBuilder.vertex(matrix, (float)startX, (float)endY, (float)z).color(rE, gE, bE, aE).next();
            bufferBuilder.vertex(matrix, (float)endX, (float)endY, (float)z).color(rE, gE, bE, aE).next();
        } else {
            bufferBuilder.vertex(matrix, (float)endX, (float)startY, (float)z).color(rE, gE, bE, aE).next();
            bufferBuilder.vertex(matrix, (float)startX, (float)startY, (float)z).color(rS, gS, bS, aS).next();
            bufferBuilder.vertex(matrix, (float)startX, (float)endY, (float)z).color(rS, gS, bS, aS).next();
            bufferBuilder.vertex(matrix, (float)endX, (float)endY, (float)z).color(rE, gE, bE, aE).next();
        }
        
        tessellator.draw();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    enum GradientDirection {
        VERTICAL,
        HORIZONTAl
    }
}
