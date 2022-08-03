package me.kalmemarq.jsonui;

import me.kalmemarq.jsonui.util.Anchor;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUIMod implements ModInitializer {
	public static final String MOD_ID = "kmjsonui";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static JsonUIConfiguration config;

	@Override
	public void onInitialize() {
		AutoConfig.register(JsonUIConfiguration.class, GsonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(JsonUIConfiguration.class).getConfig();

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new JsonUIManager());

		LOGGER.info("top_left " + Anchor.isValid("top_left"));
		LOGGER.info("top_center " + Anchor.isValid("top_center"));
	}

	public static Identifier getIdentifier(String path) {
		return path.contains(":") ? new Identifier(path) : new Identifier(MOD_ID, path);
	}
}
