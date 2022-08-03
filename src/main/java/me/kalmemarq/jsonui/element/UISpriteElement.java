package me.kalmemarq.jsonui.element;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;

import me.kalmemarq.jsonui.JsonUIClientMod;
import me.kalmemarq.jsonui.util.Color;
import me.kalmemarq.jsonui.util.math.Vector2;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class UISpriteElement extends UIElement {
    public Identifier texture = TextureManager.MISSING_IDENTIFIER;
    public Color color = Color.WHITE;
    public float alpha = 1.0f;
    public boolean bilinear = false;

    @Nullable
    public Vector2 uv = null;
    @Nullable
    public Vector2 uvSize = null;

    public UISpriteElement() {}

    public void setTexture(Identifier texture) {
        this.texture = texture;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setBilinear(boolean bilinear) {
        this.bilinear = bilinear;
    }

    public void setUV(int u, int v) {
        this.uv = new Vector2(u, v);
    }

    public void setUVSize(int regionWidth, int regionHeight) {
        this.uvSize = new Vector2(regionWidth, regionHeight);
    }

    @Override
    public void render(MinecraftClient client, MatrixStack matrices, int width, int height, int mouseX, int mouseY, float tickDelta) {
        RenderSystem.enableBlend();

        JsonUIClientMod.TextureDimensions dimensions = JsonUIClientMod.TextureDimStorage.get(this.texture);

        if (this.bilinear) client.getTextureManager().getTexture(this.texture).setFilter(true, false);
        RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderTexture(0, this.texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        if (this.uv != null && this.uvSize != null) {
            this.drawTexture(matrices, 0, this.uvSize.getX(), 0, this.uvSize.getY(), 0, this.uvSize.getX(), this.uvSize.getY(), this.uv.getX(), this.uv.getX(), dimensions.getWidth(), dimensions.getHeight());
        } else {
            this.drawTexture(matrices, 0, dimensions.getWidth(), 0, dimensions.getHeight(), 0, 256, 256, 0, 0, dimensions.getWidth(), dimensions.getHeight());
        }


        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.defaultBlendFunc();
        if (this.bilinear) client.getTextureManager().getTexture(this.texture).setFilter(false, false);
    }

    private void drawTexture(MatrixStack matrices, int x0, int x1, int y0, int y1, int z, int regionWidth, int regionHeight, float u, float v, int textureWidth, int textureHeight) {
        drawTexturedQuad(matrices.peek().getPositionMatrix(), x0, x1, y0, y1, z, (u + 0.0F) / (float)textureWidth, (u + (float)regionWidth) / (float)textureWidth, (v + 0.0F) / (float)textureHeight, (v + (float)regionHeight) / (float)textureHeight);
    }
    
    private void drawTexturedQuad(Matrix4f matrix, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1) {
        boolean isWhite = this.color.equals(Color.WHITE);

        RenderSystem.setShader(isWhite ? GameRenderer::getPositionTexShader : GameRenderer::getPositionColorTexShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(DrawMode.QUADS, isWhite ? VertexFormats.POSITION_TEXTURE : VertexFormats.POSITION_COLOR_TEXTURE);
        
        if (isWhite) {
            bufferBuilder.vertex(matrix, (float)x0, (float)y1, (float)z).texture(u0, v1).next();
            bufferBuilder.vertex(matrix, (float)x1, (float)y1, (float)z).texture(u1, v1).next();
            bufferBuilder.vertex(matrix, (float)x1, (float)y0, (float)z).texture(u1, v0).next();
            bufferBuilder.vertex(matrix, (float)x0, (float)y0, (float)z).texture(u0, v0).next();
        } else {
            bufferBuilder.vertex(matrix, (float)x0, (float)y1, (float)z).color(this.color.r, this.color.g, this.color.b, 1.0f).texture(u0, v1).next();
            bufferBuilder.vertex(matrix, (float)x1, (float)y1, (float)z).color(this.color.r, this.color.g, this.color.b, 1.0f).texture(u1, v1).next();
            bufferBuilder.vertex(matrix, (float)x1, (float)y0, (float)z).color(this.color.r, this.color.g, this.color.b, 1.0f).texture(u1, v0).next();
            bufferBuilder.vertex(matrix, (float)x0, (float)y0, (float)z).color(this.color.r, this.color.g, this.color.b, 1.0f).texture(u0, v0).next();
        }

        BufferRenderer.drawWithShader(bufferBuilder.end());
    }

    protected static final class Serializer implements IUIElementSerializer {
        @Override
        public UIElement fromJson(JsonObject obj, Map<String, PropertyBag.Property> storage) {
            UISpriteElement el = new UISpriteElement();

            if (JsonHelper.hasString(obj, "texture")) {
                el.setTexture(Identifier.tryParse(JsonHelper.getString(obj, "texture")));
            }

            if (JsonHelper.hasString(obj, "color")) {
                String c = JsonHelper.getString(obj, "color");
                if (Color.hasName(c)) {
                    el.setColor(Color.fromName(c));
                } else {
                    el.setColor(Color.fromSRGB(c));
                }
            } else if (JsonHelper.hasArray(obj, "color")) {
                JsonArray arr = JsonHelper.getArray(obj, "color");
                if (arr.size() >= 3 && arr.size() <= 4) {
                    el.setColor(Color.fromRGB(arr.get(0).getAsFloat(), arr.get(1).getAsFloat(), arr.get(2).getAsFloat()));
                }
            }

            if (JsonHelper.hasArray(obj, "uv")) {
                JsonArray arr = JsonHelper.getArray(obj, "uv");
                el.setUV(arr.get(0).getAsInt(), arr.get(1).getAsInt());
            }

            if (JsonHelper.hasArray(obj, "uv_size")) {
                JsonArray arr = JsonHelper.getArray(obj, "uv_size");
                el.setUVSize(arr.get(0).getAsInt(), arr.get(1).getAsInt());
            }

            if (JsonHelper.hasBoolean(obj, "bilinear")) {
                el.setBilinear(JsonHelper.getBoolean(obj, "bilinear"));
            }

            if (JsonHelper.hasNumber(obj, "alpha")) {
                el.setAlpha(JsonHelper.getFloat(obj, "alpha"));
            }

            if (JsonHelper.hasJsonObject(obj, "property_bag")) {
                el.setPropertyBag(PropertyBag.Serializer.fromJson(JsonHelper.getObject(obj, "property_bag")));
            }

            return el;
        }
    }
}
