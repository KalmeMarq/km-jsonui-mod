package me.kalmemarq.jsonui.util;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Anchor {
    TOP_LEFT("top_left"),
    TOP_MIDDLE("top_middle"),
    TOP_RIGHT("top_right"),
    LEFT_MIDDLE("left_middle"),
    CENTER("center"),
    RIGHT_MIDDLE("right_middle"),
    BOTTOM_LEFT("bottom_left"),
    BOTTOM_MIDDLE("bottom_middle"),
    BOTTOM_RIGHT("bottom_right");

    private static final Map<String, Anchor> ANCHORS = Arrays.stream(values()).collect(Collectors.toMap(Anchor::getName, Function.identity()));

    private final String name;

    Anchor(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static boolean isValid(String anchor) {
        return ANCHORS.containsKey(anchor);
    }

    @Nullable
    public static Anchor getFromName(String name) {
        return ANCHORS.get(name);
    }
}
