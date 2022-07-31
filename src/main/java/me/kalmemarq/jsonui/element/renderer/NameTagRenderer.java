package me.kalmemarq.jsonui.element.renderer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import me.kalmemarq.jsonui.util.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.JsonHelper;

public class NameTagRenderer extends DrawableHelper implements ICustomRenderer {
    public Color textColor = Color.fromRGBA(1.0f, 1.0f, 1.0f, 1.0f);
    public Color bgColor = Color.fromRGBA(0.5f, 0.5f, 0.5f, 0.25f);
    public String playerName = "";

    @Override
    public void init(JsonObject obj) {
        if (JsonHelper.hasString(obj, "text_color")) {
            String c = JsonHelper.getString(obj, "text_color");
            if (Color.hasName(c)) {
                textColor = (Color.fromName(c));
            } else {
                textColor = (Color.fromSRGB(c));
            }
        } else if (JsonHelper.hasArray(obj, "text_color")) {
            JsonArray arr = JsonHelper.getArray(obj, "text_color");
            if (arr.size() == 3) {
                textColor = (Color.fromRGB(arr.get(0).getAsFloat(), arr.get(1).getAsFloat(), arr.get(2).getAsFloat()));
            } else if (arr.size() == 4) {
                textColor = (Color.fromRGBA(arr.get(0).getAsFloat(), arr.get(1).getAsFloat(), arr.get(2).getAsFloat(), arr.get(3).getAsFloat()));
            }
        }

        if (JsonHelper.hasString(obj, "background_color")) {
            String c = JsonHelper.getString(obj, "background_color");
            if (Color.hasName(c)) {
                bgColor = (Color.fromName(c));
            } else {
                bgColor = (Color.fromSRGB(c));
            }
        } else if (JsonHelper.hasArray(obj, "background_color")) {
            JsonArray arr = JsonHelper.getArray(obj, "background_color");
            if (arr.size() == 3) {
                bgColor = (Color.fromRGB(arr.get(0).getAsFloat(), arr.get(1).getAsFloat(), arr.get(2).getAsFloat()));
            } else if (arr.size() == 4) {
                bgColor = (Color.fromRGBA(arr.get(0).getAsFloat(), arr.get(1).getAsFloat(), arr.get(2).getAsFloat(), arr.get(3).getAsFloat()));
            }
        }

        if (JsonHelper.hasJsonObject(obj, "property_bag")) {
            JsonObject bag = JsonHelper.getObject(obj, "property_bag");

            if (JsonHelper.hasString(bag, "#playername")) {
                playerName = JsonHelper.getString(bag, "#playername");
            }
        }
    }

    @Override
    public void render(MinecraftClient client, MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
        int w = client.textRenderer.getWidth(this.playerName);
        
        fill(matrices, 1, 1, 1 + w + 2, 1 + client.textRenderer.fontHeight + 1, this.bgColor.toColorWithAlpha());

        client.textRenderer.draw(matrices, this.playerName, 2, 2, this.textColor.toColorWithAlpha());
    }
}
