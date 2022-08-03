package me.kalmemarq.jsonui;

import me.kalmemarq.jsonui.element.PropertyBag;
import me.kalmemarq.jsonui.element.UIElement;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.AddServerScreen;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.screen.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.client.gui.screen.DirectConnectScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.BlastFurnaceScreen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.BrewingStandScreen;
import net.minecraft.client.gui.screen.ingame.CartographyTableScreen;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.client.gui.screen.ingame.GrindstoneScreen;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.client.gui.screen.ingame.JigsawBlockScreen;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import net.minecraft.client.gui.screen.ingame.SmokerScreen;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
//import net.minecraft.client.gui.screen.report.AbuseReportReasonScreen;
//import net.minecraft.client.gui.screen.report.ChatReportScreen;
//import net.minecraft.client.gui.screen.report.ChatSelectionScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

public class JsonUIClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenEvents.BEFORE_INIT.register((MinecraftClient client, Screen s, int w, int h) -> {
            String name = TempScreens.getName(s);
            Map<String, PropertyBag.Property> storage = Maps.newHashMap();
            storage.put("#game_version", new PropertyBag.Property("#game_version"));
            storage.put("#version_visible", new PropertyBag.Property("#version_visible"));

            if (name != null) {
                List<UIElement> els = JsonUIManager.getUIElements(name, storage);
                storage.get("#game_version").setValue(SharedConstants.getGameVersion().getName());
                storage.get("#version_visible").setValue(true);

                if (els.size() > 0) {
                    if (TempScreens.isValid(s) && JsonUIManager.isEnabled(name)) {
                        ScreenEvents.afterRender(s).register((Screen screen, MatrixStack matrices, int mouseX, int mouseY, float tickDelta) -> {
                            for (UIElement el : els) {
                                el.render(client, matrices, screen.width, screen.height, mouseX, mouseY, tickDelta);
                            }

                            if (mouseX > screen.width / 2) {
                                storage.get("#version_visible").setValue(false);
                            } else {
                                storage.get("#version_visible").setValue(true);
                            }
                        });
                    }
                }
            }
        });
    }

    public static class TextureDimStorage {
        public static final TextureDimensions FALLBACK_DIM = new TextureDimensions(256, 256);
        public static Map<Identifier, TextureDimensions> storage = Maps.newHashMap();

        public static boolean has(Identifier identifier) {
            return storage.containsKey(identifier);
        }

        public static TextureDimensions get(Identifier identifier) {
            TextureDimensions dim = storage.get(identifier);

            if (dim == null) {
                add(identifier);
            }

            return dim == null ? FALLBACK_DIM : dim;
        }

        public static void reload() {
            storage.clear();
        }

        public static void add(Identifier identifier) {
            try {
                Resource resource = MinecraftClient.getInstance().getResourceManager().getResourceOrThrow(identifier);
                InputStream inputStream = resource.getInputStream();

                NativeImage nativeImage = null;
                nativeImage = NativeImage.read(inputStream);

                storage.put(identifier, new TextureDimensions(nativeImage.getWidth(), nativeImage.getHeight()));
            } catch (Exception e) {
                JsonUIMod.LOGGER.info("Failed to get texture dimensions for " + identifier);
            }
        }
    }

    public static class TextureDimensions {
        private final int width;
        private final int height;

        public TextureDimensions(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public String toString() {
            return "TextureDim[width=" + this.width + ",height=" + this.height + "]";
        }
    }

    public static class TempScreens {
        private static final Map<String, SCheckFunc> TEMP = Maps.newHashMap();

        static {
            TEMP.put("title", (Screen screen) -> screen instanceof TitleScreen);
            TEMP.put("select_world", (Screen screen) -> screen instanceof SelectWorldScreen);
            TEMP.put("world_create", (Screen screen) -> screen instanceof CreateWorldScreen);
            TEMP.put("customize_buffet_level", (Screen screen) -> screen instanceof CustomizeBuffetLevelScreen);
            TEMP.put("customize_flat_level", (Screen screen) -> screen instanceof CustomizeFlatLevelScreen);
            TEMP.put("world_edit", (Screen screen) -> screen instanceof EditWorldScreen);
            TEMP.put("multiplayer", (Screen screen) -> screen instanceof MultiplayerScreen);
            TEMP.put("multiplayer_warning", (Screen screen) -> screen instanceof MultiplayerWarningScreen);
            TEMP.put("social_interactions", (Screen screen) -> screen instanceof SocialInteractionsScreen);
            TEMP.put("add_server", (Screen screen) -> screen instanceof AddServerScreen);
            TEMP.put("direct_connect", (Screen screen) -> screen instanceof DirectConnectScreen);
            TEMP.put("realms_main", (Screen screen) -> screen instanceof RealmsMainScreen);
            TEMP.put("options", (Screen screen) -> screen instanceof OptionsScreen);
            TEMP.put("game_menu", (Screen screen) -> screen instanceof GameMenuScreen);
            TEMP.put("sleeping_chat", (Screen screen) -> screen instanceof SleepingChatScreen);
            TEMP.put("death", (Screen screen) -> screen instanceof DeathScreen);
            TEMP.put("chat", (Screen screen) -> screen instanceof ChatScreen);
            TEMP.put("advancements", (Screen screen) -> screen instanceof AdvancementsScreen);
            TEMP.put("anvil", (Screen screen) -> screen instanceof AnvilScreen);
            TEMP.put("beacon", (Screen screen) -> screen instanceof BeaconScreen);
            TEMP.put("blast_furnace", (Screen screen) -> screen instanceof BlastFurnaceScreen);
            TEMP.put("book", (Screen screen) -> screen instanceof BookScreen);
            TEMP.put("brewing_stand", (Screen screen) -> screen instanceof BrewingStandScreen);
            TEMP.put("cartography_table", (Screen screen) -> screen instanceof CartographyTableScreen);
            TEMP.put("enchanting_table", (Screen screen) -> screen instanceof EnchantmentScreen);
            TEMP.put("furnace", (Screen screen) -> screen instanceof FurnaceScreen);
            TEMP.put("gridstone", (Screen screen) -> screen instanceof GrindstoneScreen);
            TEMP.put("hopper", (Screen screen) -> screen instanceof HopperScreen);
            TEMP.put("horse", (Screen screen) -> screen instanceof HorseScreen);
            TEMP.put("jigsaw_block", (Screen screen) -> screen instanceof JigsawBlockScreen);
            TEMP.put("loom", (Screen screen) -> screen instanceof LoomScreen);
            TEMP.put("merchant", (Screen screen) -> screen instanceof MerchantScreen);
            TEMP.put("smithing", (Screen screen) -> screen instanceof SmithingScreen);
            TEMP.put("smoker", (Screen screen) -> screen instanceof SmokerScreen);
            TEMP.put("stonecutter", (Screen screen) -> screen instanceof StonecutterScreen);
            TEMP.put("structure_block", (Screen screen) -> screen instanceof StructureBlockScreen);
            TEMP.put("demo", (Screen screen) -> screen instanceof DemoScreen);
            TEMP.put("disconnected", (Screen screen) -> screen instanceof DisconnectedScreen);
//            TEMP.put("abuse_report_reason", (Screen screen) -> screen instanceof AbuseReportReasonScreen);
//            TEMP.put("chat_report", (Screen screen) -> screen instanceof ChatReportScreen);
//            TEMP.put("chat_selection", (Screen screen) -> screen instanceof ChatSelectionScreen);
        }

        @Nullable
        public static String getName(Screen screen) {
            for (Map.Entry<String, SCheckFunc> e : TEMP.entrySet()) {
                if (e.getValue().check(screen)) {
                    return e.getKey();
                }
            }
            
            return null;
        }
  
        public static boolean isValid(Screen screen) {
            boolean is = false;

            for (SCheckFunc e : TEMP.values()) {
                if (e.check(screen)) {
                    is = true;
                    break;
                }
            }

            return is;
        }

        interface SCheckFunc {
            boolean check(Screen screen);
        }
    }
}
