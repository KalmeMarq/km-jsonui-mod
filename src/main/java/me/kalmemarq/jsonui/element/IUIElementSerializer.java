package me.kalmemarq.jsonui.element;

import com.google.gson.JsonObject;

public interface IUIElementSerializer {
    public UIElement fromJson(JsonObject obj);
}
