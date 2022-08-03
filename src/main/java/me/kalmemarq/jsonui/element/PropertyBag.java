package me.kalmemarq.jsonui.element;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kalmemarq.jsonui.JsonUIMod;
import net.minecraft.util.JsonHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PropertyBag {
    private final Map<String, Property> properties = Maps.newHashMap();

    public void addProperty(String propertyName) {
        if (!this.properties.containsKey(propertyName)) this.properties.put(propertyName, new Property(propertyName));
    }

    public void addProperty(String propertyName, Object value) {
        if (this.properties.containsKey(propertyName)) {
            this.properties.get(propertyName).setValue(value);
        } else {
            this.properties.put(propertyName, new Property(propertyName));
            this.properties.get(propertyName).setValue(value);
        }
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public void subscribe(String propertyName, IObserver observer) {
        if (!this.properties.containsKey(propertyName)) {
            this.properties.put(propertyName, new Property(propertyName));
        }

        this.properties.get(propertyName).subscribe(observer);
    }

    public void setValue(String propertyName, Object newValue) {
        if (!this.properties.containsKey(propertyName)) {
            this.properties.put(propertyName, new Property(propertyName));
        }

        this.properties.get(propertyName).setValue(newValue);
    }

    public Property getProperty(String propertyName) {
        if (!this.properties.containsKey(propertyName)) {
            this.properties.put(propertyName, new Property(propertyName));
        }

        return this.properties.get(propertyName);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");

        for (Map.Entry<String, Property> property : properties.entrySet()) {
            builder.append(property.getKey()).append("=").append(property.getValue().getAsString());
            builder.append(",");
        }

        builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append("]");

        return builder.toString();
    }

    public static class Serializer {
        public static PropertyBag fromJson(JsonObject obj) {
            PropertyBag bag = new PropertyBag();

            for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
                JsonElement v = e.getValue();

                if (JsonHelper.isBoolean(v)) {
                    bag.addProperty(e.getKey(), v.getAsBoolean());
                } else if (JsonHelper.isNumber(v)) {
                    bag.addProperty(e.getKey(), v.getAsFloat());
                } else if (JsonHelper.isString(v)) {
                    bag.addProperty(e.getKey(), v.getAsString());
                }
            }

            JsonUIMod.LOGGER.info("PB: " + bag.toString());

            return bag;
        }
    }

    public static class Property {
        private final List<IObserver> observers = new ArrayList<>();
        private Object value;
        private final String name;

        public Property(String name) {
            this.name = name;
        }

        public void setValue(Object newValue) {
            this.value = newValue;
            this.notifyObservers();
        }

        public void subscribe(IObserver observer) {
            this.observers.add(observer);
        }

        public void notifyObservers() {
            for (IObserver observer : observers) {
                observer.observe(this.value, this);
            }
        }

        public String getAsString() {
            try {
                return String.valueOf(value);
            } catch (Exception ignored) {
                return this.name;
            }
        }

        public boolean getAsBoolean() {
            try {
                return !(value == Boolean.FALSE);
            } catch (Exception e) {
                return true;
            }
        }

        public int getAsInt() {
            try {
                return (int)getAsFloat();
            } catch (Exception ignored) {
                return 0;
            }
        }

        public float getAsFloat() {
            try {
                return Float.parseFloat(String.valueOf(value));
            } catch (Exception ignored) {
                return 0.0f;
            }
        }
    }

    public interface IObserver {
        void observe(Object newValue, Property property);
    }
}
