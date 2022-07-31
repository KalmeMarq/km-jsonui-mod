package me.kalmemarq.jsonui.element.renderer;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class InventoryItemRenderer implements ICustomRenderer {
    @Nullable
    public Item item;
    
    public InventoryItemRenderer() {}

    public void init(JsonObject obj) {
        if (JsonHelper.hasJsonObject(obj, "property_bag")) {
            JsonObject bag = JsonHelper.getObject(obj, "property_bag");

            if (JsonHelper.hasString(bag, "#item_id")) {
                Identifier id = Identifier.tryParse(JsonHelper.getString(bag, "#item_id"));

                if (id != null) {
                    if (Registry.ITEM.containsId(id)) {
                        this.item = Registry.ITEM.get(id);
                    }
                }
            }
        }
    }

    public void render(MinecraftClient client, MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
        if (this.item != null) {
            client.getItemRenderer().renderGuiItemIcon(new ItemStack(this.item), 110, 1);
        }
    }
}
