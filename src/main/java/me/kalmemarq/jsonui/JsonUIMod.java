package me.kalmemarq.jsonui;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUIMod implements ModInitializer {
	public static final String MOD_ID = "kmjsonui";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new JsonUIManager());
	}

	public static Identifier getIdentifier(String path) {
		return path.contains(":") ? new Identifier(path) : new Identifier(MOD_ID, path);
	}
}
