package me.kalmemarq.jsonui.element;

import java.util.ArrayList;
import java.util.List;

public class DataBinding {
    public List<Binding> bindings = new ArrayList<>();

    public static class Binding {
        public BindingCondition condition = BindingCondition.NONE;

        public Binding() {
        }
    }

    public enum BindingCondition {
        NONE,
        VISIBLE,
        ONCE,
        VISIBILITY_CHANGED
    }
}
