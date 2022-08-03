package me.kalmemarq.jsonui.element;

import com.google.gson.JsonObject;

import java.util.Map;

public interface IUIElementSerializer {
    public UIElement fromJson(JsonObject obj, Map<String, PropertyBag.Property> storage);
}
