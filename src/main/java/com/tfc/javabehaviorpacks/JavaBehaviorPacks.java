package com.tfc.javabehaviorpacks;

import com.tfc.javabehaviorpacks.registry_handlers.Block;
import com.tfc.javabehaviorpacks.registry_handlers.Item;
import net.fabricmc.api.ModInitializer;

import java.io.File;
import java.util.ArrayList;

public class JavaBehaviorPacks implements ModInitializer {
	public static ArrayList<String> namespaces = new ArrayList<>();
	
	@Override
	public void onInitialize() {
		File bevPacks = new File("behavior_packs");
		if (!bevPacks.exists()) {
			bevPacks.mkdirs();
		}
		try {
			for (File pack : bevPacks.listFiles()) {
				for (File resource : pack.listFiles()) {
					if (resource.getName().equals("items")) {
						for (File item : resource.listFiles()) {
							Item.register(item);
						}
					}
					if (resource.getName().equals("blocks")) {
						for (File item : resource.listFiles()) {
							Block.register(item);
						}
					}
				}
			}
		} catch (Throwable ignored) {
		}
	}
}
