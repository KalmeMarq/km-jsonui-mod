package me.kalmemarq.jsonui.util;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Alignment {
    LEFT("left"),
    CENTER("center"),
    RIGHT("right");

    private static final Map<String, Alignment> ALIGNMENTS = Arrays.stream(values()).collect(Collectors.toMap(Alignment::getName, Function.identity()));

    private final String name;

    Alignment(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static boolean isValid(String Alignment) {
        return ALIGNMENTS.containsKey(Alignment);
    }

    @Nullable
    public static Alignment getFromName(String name) {
        return ALIGNMENTS.get(name);
    }

    public static Alignment getFromName(Anchor anchor) {
        if (anchor == Anchor.TOP_LEFT || anchor == Anchor.LEFT_MIDDLE || anchor == Anchor.BOTTOM_LEFT) {
            return Alignment.LEFT;
        } else if (anchor == Anchor.TOP_RIGHT || anchor == Anchor.RIGHT_MIDDLE || anchor == Anchor.BOTTOM_RIGHT) {
            return Alignment.RIGHT;
        }

        return Alignment.CENTER;
    }
}

