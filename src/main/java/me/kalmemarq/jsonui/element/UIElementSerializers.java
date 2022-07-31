package me.kalmemarq.jsonui.element;

import com.google.common.collect.Maps;
import me.kalmemarq.jsonui.JsonUIMod;
import net.minecraft.util.Identifier;

import java.util.Map;

public class UIElementSerializers {
    private static final Map<Identifier, IUIElementSerializer> SERIALIZERS = Maps.newHashMap();

    static {
        register("label", new UILabelElement.Serializer());
        register("image", new UISpriteElement.Serializer());
        register("custom", new UICustomElement.Serializer());
    }

    public static void register(String name, IUIElementSerializer serializer) {
        SERIALIZERS.put(JsonUIMod.getIdentifier(name), serializer);
    }

    public static void register(Identifier id, IUIElementSerializer serializer) {
        SERIALIZERS.put(id, serializer);
    }

    public static IUIElementSerializer get(String name) {
        return SERIALIZERS.get(JsonUIMod.getIdentifier(name));
    }

    public static IUIElementSerializer get(Identifier id) {
        return SERIALIZERS.get(id);
    }
}
