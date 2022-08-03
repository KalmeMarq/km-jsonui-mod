package me.kalmemarq.jsonui.element.renderer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import me.kalmemarq.jsonui.element.PropertyBag;
import me.kalmemarq.jsonui.element.UICustomElement;
import me.kalmemarq.jsonui.util.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.JsonHelper;

import java.awt.desktop.SystemEventListener;
import java.util.Random;

public class NameTagRenderer extends DrawableHelper implements ICustomRenderer {
    Random RANDOM = new Random();

    public Color textColor = Color.fromRGBA(1.0f, 1.0f, 1.0f, 1.0f);
    public Color bgColor = Color.fromRGBA(0.5f, 0.5f, 0.5f, 0.5f);
    public String playerName = "";
    public int xPadding = 0;
    public int yPadding = 0;

    PropertyBag bag;

    int x = 10;
    int y = 10;

    @Override
    public void init(JsonObject obj, UICustomElement element) {
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

        PropertyBag propertyBag = element.getPropertyBag();
        bag = propertyBag;

        propertyBag.subscribe("#playername", (newValue, property) -> {
            playerName = property.getAsString();
        });

        propertyBag.subscribe("#x_padding", ((newValue, property) -> {
            xPadding = property.getAsInt();
        }));

        propertyBag.subscribe("#y_padding", ((newValue, property) -> {
            yPadding = property.getAsInt();
        }));
    }

    @Override
    public void render(MinecraftClient client, MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
        int w = client.textRenderer.getWidth(this.playerName);

        int x1 = x - xPadding - 1;
        int y1 = y - yPadding - 1;
        int x2 = x + w + xPadding;
        int y2 = y + client.textRenderer.fontHeight + yPadding;

        fill(matrices, x1 - 1, y1 - 1, x2 + 1, y2 + 1, this.bgColor.toColorWithAlpha());

        client.textRenderer.draw(matrices, this.playerName, x, y, this.textColor.toColorWithAlpha());

        if (bag != null) {
            if (RANDOM.nextInt(10) == 1) {
                bag.setValue("#x_padding", RANDOM.nextInt(0, 20));
            } else if (RANDOM.nextInt(10) == 2) {
                bag.setValue("#y_padding", RANDOM.nextInt(0, 20));
            }
        }
    }
}
