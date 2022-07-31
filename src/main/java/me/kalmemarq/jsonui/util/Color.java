package me.kalmemarq.jsonui.util;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Color {
    private static Map<String, Color> COLOR_NAMES = Maps.newHashMap();

    public static Color BLACK = new Color("black", 0x000000);
    public static Color WHITE = new Color("white", 0xFFFFFF);
    public static Color RED = new Color("red", 0xFF5555);
    public static Color GREEN = new Color("green", 0x55FF55);
    public static Color BLUE = new Color("blue", 0x5555FF);
    public static Color GOLD = new Color("gold", 0xFFAA00);
    public static Color GRAY = new Color("gray", 0xAAAAAA);
    public static Color AQUA = new Color("aqua", 0x55FFFF);
    public static Color YELLOW = new Color("yellow", 0xFFFFAA);
    public static Color LIGHT_PURPLE = new Color("light_purple", 0xFF55FF);
    public static Color DARK_BLUE = new Color("dark_blue", 0x0000AA);
    public static Color DARK_GREEN = new Color("dark_green", 0x00AA00);
    public static Color DARK_AQUA = new Color("dark_aqua", 0x00AAAA);
    public static Color DARK_RED = new Color("dark_red", 0xAA0000);
    public static Color DARK_PURPLE = new Color("dark_purple", 0xAA00AA);
    public static Color DARK_GRAY = new Color("dark_gray", 0x555555);

    @Nullable
    public String name;
    public float r;
    public float g;
    public float b;
    public float a;

    private Color(float r, float g, float b, float a) {
        this(null, r, g, b, a);
    }

    private Color(@Nullable String name, int color) {
        Color c = Color.fromRGB(color);
        this.name = name;
        this.r = c.r;
        this.g = c.g;
        this.b = c.b;
        this.a = c.a;

        if (name != null) COLOR_NAMES.put(name, this);
    }

    private Color(@Nullable String name, float r, float g, float b, float a) {
        this.name = name;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;

        if (name != null) COLOR_NAMES.put(name, this);
    }

    public String toString() {
        return "Color[name=" + this.name + ",red=" + this.r + ",green=" + this.g + ",blue=" + this.b + "]";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Color) {
            Color m = (Color)o;
            return m.r == this.r && m.g == this.g && m.b == this.b && m.a == this.a;
        }

        return false;
    }

    public int toColor() {
        return (int)(this.r * 255) << 16 | (int)(this.g * 255) << 8 | (int)(this.b * 255);
    }

    public static Color fromRGB(float red, float green, float blue) {
        return new Color(red, green, blue, 1.0f);
    }

    public static Color fromRGB(int red, int green, int blue) {
        return new Color(red / 255.0f, green / 255.0f, blue / 255.0f, 1.0f);
    }

    public static Color fromRGBA(float red, float green, float blue, float alpha) {
        return new Color(red, green, blue, alpha);
    }

    public static Color fromRGB(int color) {
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        return new Color(red / 255.0f, green / 255.0f, blue / 255.0f, 1.0f);
    }

    public static Color fromName(String name) {
        return COLOR_NAMES.getOrDefault(name, Color.WHITE);
    }

    public static boolean hasName(String name) {
        return COLOR_NAMES.containsKey(name);
    }

    @Nullable
    public static Color fromSRGB(String str) {
        str = str.trim();

        if (str.startsWith("rgb(") && str.endsWith(")")) {
            str = str.substring(4, str.length() - 1);
            List<Integer> c = Arrays.stream(str.split(",")).map(String::trim).map(Integer::parseInt).collect(Collectors.toList());

            return Color.fromRGB(c.get(0), c.get(1), c.get(2));
        } else if (str.startsWith("#")) {
            str = str.substring(1);

            if (str.length() == 3) {
                String r = str.substring(0, 1);
                r = r + r;
                String g = str.substring(1, 2);
                g = g + g;
                String b = str.substring(2, 3);
                b = b + b;

                int c = Integer.parseInt(r + g + b, 16);
                return Color.fromRGB(c);
            } else if (str.length() == 6) {
                int c = Integer.parseInt(str, 16);
                return Color.fromRGB(c);
            }
        }

        return null;
    }
}
