package me.kalmemarq.jsonui;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.text.Text;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "kmjsonui")
public class JsonUIConfiguration implements ConfigData {
    boolean hidePaperdoll = true;
    boolean screenAnimations = false;

    @Comment("""
        Style for the UI
        CLASSIC: Java UI style
        POCKET: Old pocket edition UI style""")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    UIProfile uiProfile = UIProfile.POCKET;

    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    int safeArea = 100;

    boolean copyCoordinateUI = false;

    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    int hudOpacity = 100;

    public enum UIProfile {
        POCKET,
        CLASSIC;

        @Override
        public String toString() {
            return "kmjsonui.uiProfile." + this.name().toLowerCase();
        }
    }
}
