package me.kalmemarq.jsonui;

import com.google.common.collect.Maps;
import com.google.gson.*;
import me.kalmemarq.jsonui.element.IUIElementSerializer;
import me.kalmemarq.jsonui.element.PropertyBag;
import me.kalmemarq.jsonui.element.UIElement;
import me.kalmemarq.jsonui.element.UIElementSerializers;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonUIManager implements SimpleSynchronousResourceReloadListener {
    private static final Identifier GLOBAL_VARS = JsonUIMod.getIdentifier("ui/_global_variables.json");
    private static final Identifier UI_DEFS = JsonUIMod.getIdentifier("ui/_ui_defs.json");

    public static Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();

    public static Map<String, JsonObject> namespaceDef = Maps.newHashMap();
    public static Map<String, Boolean> namespaceDefEnabled = Maps.newHashMap();

    @Override
    public Identifier getFabricId() {
        return JsonUIMod.getIdentifier("json_ui");
    }

    @Override
    public void reload(ResourceManager manager) {
        JsonUIClientMod.TextureDimStorage.reload();

        List<Identifier> uiDeflist = new ArrayList<>();

        Map<String, JsonObject> nsDef = Maps.newHashMap();
        Map<String, Boolean> nsDefEnabled = Maps.newHashMap();

        for (Resource res : manager.getAllResources(UI_DEFS)) {
            try {
                BufferedReader reader = res.getReader();

                try {
                    JsonObject obj = GSON.fromJson(reader, JsonObject.class);

                    if (JsonHelper.hasBoolean(obj, "replace")) {
                        if (JsonHelper.getBoolean(obj, "replace")) uiDeflist.clear();
                    }

                    if (JsonHelper.hasArray(obj, "ui_defs")) {
                        JsonArray arr = JsonHelper.getArray(obj, "ui_defs");

                        for (JsonElement e : arr) {
                            if (JsonHelper.isString(e)) uiDeflist.add(JsonUIMod.getIdentifier(e.getAsString()));
                        }
                    }
                } catch (Exception e) {
                    if (reader != null) {
                        reader.close();
                    }

                    throw e;
                }

                reader.close();
            } catch (Exception e) {
                JsonUIMod.LOGGER.info("Failed to load _ui_defs.json from " + res.getResourcePackName());
            }
        }

        JsonUIMod.LOGGER.info("UI Defs: \n" + GSON.toJson(uiDeflist));

        for (Identifier uiDefItem : uiDeflist) {
            for (Resource res : manager.getAllResources(uiDefItem)) {
                try {
                    BufferedReader reader = res.getReader();

                    try {
                        JsonObject obj = GSON.fromJson(reader, JsonObject.class);

                        if (JsonHelper.hasBoolean(obj, "enabled")) {
                            nsDefEnabled.put(obj.get("namespace").getAsString(), JsonHelper.getBoolean(obj, "enabled"));
                        }

                        obj.remove("enabled");
                        String namespace = obj.get("namespace").getAsString();
                        obj.remove("namespace");

                        nsDef.put(namespace, obj);
                    } catch (Exception e) {
                        if (reader != null) {
                            reader.close();
                        }

                        throw e;
                    }

                    reader.close();
                } catch (Exception e) {
                    JsonUIMod.LOGGER.info("Failed to load " + uiDefItem + " from " + res.getResourcePackName());
                }
            }

            JsonUIMod.LOGGER.info("NS Defs: \n" + GSON.toJson(nsDef));

            namespaceDef = nsDef;
            namespaceDefEnabled = nsDefEnabled;
        }
    }

    public static boolean isEnabled(String namespace) {
        return namespaceDefEnabled.getOrDefault(namespace, false);
    }

    public static List<UIElement> getUIElements(String namespace, Map<String, PropertyBag.Property> storage) {
        List<UIElement> els = new ArrayList<>();

        JsonObject obj = namespaceDef.get(namespace);

        if (obj != null) {
            for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
                JsonObject eObj = e.getValue().getAsJsonObject();
                String type = JsonHelper.getString(eObj, "type");

                IUIElementSerializer serializer = UIElementSerializers.get(type);
                els.add(serializer.fromJson(eObj, storage));
            }
        }

        return els;
    }
}
