package me.kalmemarq.jsonui.element.renderer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;

import me.kalmemarq.jsonui.element.UICustomElement;
import me.kalmemarq.jsonui.util.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Matrix4f;

public class FillRenderer implements ICustomRenderer {
    public Color color = Color.WHITE;

    public FillRenderer() {
    }

    public void init(JsonObject obj, UICustomElement element) {
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

    public void render(MinecraftClient client, MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        int x1 = 0;
        int y1 = 0;
        int x2 = 100;
        int y2 = 100;

        float r = this.color.r;
        float g = this.color.g;
        float b = this.color.b;
        float a = this.color.a;

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, (float)x1, (float)y2, 0.0F).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix, (float)x2, (float)y2, 0.0F).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix, (float)x2, (float)y1, 0.0F).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix, (float)x1, (float)y1, 0.0F).color(r, g, b, a).next();
        BufferRenderer.drawWithShader(bufferBuilder.end());
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
