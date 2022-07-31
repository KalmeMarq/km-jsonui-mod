package me.kalmemarq.jsonui.element.renderer;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import me.kalmemarq.jsonui.JsonUIMod;
import net.minecraft.util.Identifier;

public class CustomRenderers {
    private static Map<Identifier, Func<?>> MAP = Maps.newHashMap();

    public static Func<FillRenderer> FILL_RENDERER = register("fill_renderer", FillRenderer::new);
    public static Func<GradientRenderer> GRADIENT_RENDERER = register("gradient_renderer", GradientRenderer::new);
    public static Func<InventoryItemRenderer> ITEM_RENDERER = register("inventory_item_renderer", InventoryItemRenderer::new);
    public static Func<SplashTextRenderer> SPLASH_TEXT_RENDERER = register("splash_text_renderer", SplashTextRenderer::new);
    public static Func<NameTagRenderer> NAME_TAG_RENDERER = register("name_tag_renderer", NameTagRenderer::new);

    public static <T extends ICustomRenderer> Func<T> register(String name, Func<T> customRenderer) {
        MAP.put(JsonUIMod.getIdentifier(name), customRenderer);
        return customRenderer;
    }

    public static <T extends ICustomRenderer> Func<T> register(Identifier id, Func<T> customRenderer) {
        MAP.put(id, customRenderer);
        return customRenderer;
    }

    @Nullable
    public static Func<?> get(Identifier id) {
        return MAP.get(id);
    }

    public interface Func<T extends ICustomRenderer> {
        T create();
    }
}
