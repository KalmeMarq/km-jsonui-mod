package me.kalmemarq.jsonui.element;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;

import me.kalmemarq.jsonui.util.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.Map;

public class UILabelElement extends UIElement {
    public String text = "";
    public Color color = Color.WHITE;
    public boolean shadow = true;
    public Identifier fontType = Style.DEFAULT_FONT_ID;
    public boolean localize = true;
    public boolean visible = true;
    public float alpha = 1.0f;

    public UILabelElement() {}

    public void setText(String text) {
        if (text.startsWith("#")) {
            this.getPropertyBag().subscribe(text, ((newValue, property) -> {
                this.text = property.getAsString();
            }));
        } else {
            this.text = text;
        }
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setLocalize(boolean localize) {
        this.localize = localize;
    }

    public void setFontType(Identifier fontType) {
        this.fontType = fontType;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    @Override
    public void render(MinecraftClient client, MatrixStack matrices, int width, int height, int mouseX, int mouseY, float tickDelta) {
        if (this.visible) {
            MutableText txt = this.localize ? Text.translatable(this.text) : Text.literal(this.text);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
            if (this.shadow) {
                client.textRenderer.drawWithShadow(matrices, txt.fillStyle(Style.EMPTY.withFont(this.fontType)), 0, 0, this.color.toColor());
            } else {
                client.textRenderer.draw(matrices, txt.fillStyle(Style.EMPTY.withFont(this.fontType)), 0, 0, this.color.toColor());
            }
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    protected static final class Serializer implements IUIElementSerializer {
        @Override
        public UIElement fromJson(JsonObject obj, Map<String, PropertyBag.Property> storage) {
            UILabelElement el = new UILabelElement();

            if (JsonHelper.hasString(obj, "text")) {
                el.setText(JsonHelper.getString(obj, "text"));
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

            if (JsonHelper.hasBoolean(obj, "shadow")) {
                el.setShadow(JsonHelper.getBoolean(obj, "shadow"));
            }

            if (JsonHelper.hasString(obj, "font_type")) {
                Identifier font = Identifier.tryParse(JsonHelper.getString(obj, "font_type"));
                if (font != null) el.setFontType(font);
            }

            if (JsonHelper.hasBoolean(obj, "localize")) {
                el.setLocalize(JsonHelper.getBoolean(obj, "localize"));
            }

            if (JsonHelper.hasNumber(obj, "alpha")) {
                el.setAlpha(JsonHelper.getFloat(obj, "alpha"));
            }

            if (JsonHelper.hasJsonObject(obj, "property_bag")) {
                el.setPropertyBag(PropertyBag.Serializer.fromJson(JsonHelper.getObject(obj, "property_bag")));
            }

            if (JsonHelper.hasArray(obj, "bindings")) {
                JsonArray bindArr = JsonHelper.getArray(obj, "bindings");

                for (JsonElement e : bindArr) {
                    JsonObject eObj = e.getAsJsonObject();

                    String bindName = eObj.get("binding_name").getAsString();
                    String bindNameOv = eObj.get("binding_name_override").getAsString();

                    PropertyBag.Property prop = storage.get(bindName);

                    if (prop != null) {
                        prop.subscribe(((newValue, property) -> {
                            if (bindNameOv.equals("#visible")) {
                                el.setVisible(property.getAsBoolean());
                            } else {
                                el.getPropertyBag().setValue(bindNameOv, property.getAsString());
                            }
                        }));
                    }
                }
            }

            return el;
        }
    }
}
